package io.github.raverbury.aggroindicator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.raverbury.aggroindicator.client.AlertRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    // 1 persistent PoseStack to avoid frequent alloc spam -> more GC
    private static PoseStack aggroindicator$staticPoseStack = new PoseStack();

    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Shadow
    public abstract boolean isSectionCompiledAndVisible(BlockPos blockPos);

    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    /**
     * Nvm we're back to submitEntities
     *
     * @param instance
     * @param original
     */
    @WrapOperation(method = "submitFeatures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    private void aggroindicator$submitAggroIcons(LevelRenderer instance, PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector output, Operation<Void> original) {
        Minecraft clientInstance = Minecraft.getInstance();
        ClientLevel level = clientInstance.level;
        Camera actualCamera = clientInstance.gameRenderer.mainCamera();
        Frustum frustum = actualCamera.getCullFrustum();
        Vec3 renderCamPos = this.levelRenderState.cameraRenderState.pos;
        double camX = renderCamPos.x();
        double camY = renderCamPos.y();
        double camZ = renderCamPos.z();
        DeltaTracker deltaTracker = clientInstance.getDeltaTracker();
        TickRateManager tickRateManager = level.tickRateManager();
        for (Entity entity : clientInstance.level.entitiesForRendering()) {
            if (this.entityRenderDispatcher.shouldRender(entity, frustum,
                    camX, camY, camZ) || entity.hasIndirectPassenger(clientInstance.player)) {
                BlockPos blockPos = entity.blockPosition();
                if ((level.isOutsideBuildHeight(blockPos.getY()) || this.isSectionCompiledAndVisible(blockPos)) && (entity != actualCamera.entity() || actualCamera.isDetached() || actualCamera.entity() instanceof LivingEntity && ((LivingEntity) actualCamera.entity()).isSleeping()) && (!(entity instanceof LocalPlayer) || actualCamera.entity() == entity)) {
                    if (entity.tickCount == 0) {
                        entity.xOld = entity.getX();
                        entity.yOld = entity.getY();
                        entity.zOld = entity.getZ();
                    }
                    while (!aggroindicator$staticPoseStack.isEmpty()) {
                        aggroindicator$staticPoseStack.popPose();
                    }
                    float partialTick =
                            deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
                    AlertRenderer.submitAggroIconForEntity(entity, partialTick,
                            aggroindicator$staticPoseStack, submitNodeStorage, entityRenderDispatcher.camera);
                    AlertRenderer.increaseSeenFrameCountForEntity(entity.getUUID(), entity.level().getGameTime());
                }
            }
        }
        original.call(instance, poseStack, levelRenderState, output);
    }
}
