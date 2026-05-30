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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
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
    private @Nullable ClientLevel level;

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract boolean isSectionCompiledAndVisible(BlockPos blockPos);

    /**
     * This actually matches LevelRenderStage events better since it's in
     * the main pass, downside is gotta iter over entities again
     * @param instance
     * @param original
     */
    @WrapOperation(method = "lambda$addMainPass$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderTranslucentFeatures()V"))
    private void aggroindicator$drawAlertIcons(FeatureRenderDispatcher instance, Operation<Void> original) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        Frustum frustum = camera.getCullFrustum();
        Vec3 renderCamPos = this.levelRenderState.cameraRenderState.pos;
        double camX = renderCamPos.x();
        double camY = renderCamPos.y();
        double camZ = renderCamPos.z();
        DeltaTracker deltaTracker = this.minecraft.getDeltaTracker();
        TickRateManager tickRateManager = this.minecraft.level.tickRateManager();
        for (Entity entity : this.level.entitiesForRendering()) {
            if (this.entityRenderDispatcher.shouldRender(entity, frustum,
                    camX, camY, camZ) || entity.hasIndirectPassenger(this.minecraft.player)) {
                BlockPos blockPos = entity.blockPosition();
                if ((this.level.isOutsideBuildHeight(blockPos.getY()) || this.isSectionCompiledAndVisible(blockPos)) && (entity != camera.entity() || camera.isDetached() || camera.entity() instanceof LivingEntity && ((LivingEntity) camera.entity()).isSleeping()) && (!(entity instanceof LocalPlayer) || camera.entity() == entity)) {
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
                    MultiBufferSource multiBufferSource = this.renderBuffers.bufferSource();
                    AlertRenderer.renderAlertIconForEntity(entity, partialTick,
                            aggroindicator$staticPoseStack, multiBufferSource, entityRenderDispatcher.camera);
                    AlertRenderer.increaseSeenFrameCountForEntity(entity.getUUID(), entity.level().getGameTime());
                }
            }
        }
        original.call(instance);
    }
}
