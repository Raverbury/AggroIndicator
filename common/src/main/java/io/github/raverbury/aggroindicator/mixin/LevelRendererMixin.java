package io.github.raverbury.aggroindicator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.raverbury.aggroindicator.client.AlertRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow @Final private RenderBuffers renderBuffers;

    /**
     * Used to be after render particle but this seems better (and the repos I'm referring to mostly use these)
     *
     * @return
     */
    @WrapOperation(method = "extractVisibleEntities",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"))
    private EntityRenderState aggroindicator$drawAlertIcons (LevelRenderer instance, Entity entity, float partialTick, Operation<EntityRenderState> original)
    {
        EntityRenderState res = original.call(instance, entity, partialTick);
        PoseStack poseStack = new PoseStack();
        MultiBufferSource multiBufferSource = this.renderBuffers.bufferSource();
        Camera camera = entityRenderDispatcher.camera;
        AlertRenderer.renderAlertIconForEntity(entity, partialTick,
                poseStack, multiBufferSource, entityRenderDispatcher.camera);
        AlertRenderer.increaseSeenFrameCountForEntity(entity.getUUID(), entity.level().getGameTime());
        return res;
    }
}
