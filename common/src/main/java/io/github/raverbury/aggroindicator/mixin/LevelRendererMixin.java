package io.github.raverbury.aggroindicator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.raverbury.aggroindicator.client.AlertRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    /**
     * Mixin to recreate LevelRenderEvent.AfterParticles in Neo/Forge
     * @param deltaTracker
     * @param renderBlockOutline
     * @param camera
     * @param gameRenderer
     * @param lightTexture
     * @param frustumMatrix
     * @param projectionMatrix
     * @param ci
     * @param poseStack
     */
    @Inject(method = "renderLevel", at = @At(value = "INVOKE_STRING", target =
            "Lnet/minecraft/util/profiling/ProfilerFiller;popPush" +
                    "(Ljava/lang/String;)V", args = "ldc=particles", shift =
            At.Shift.AFTER))
    private void aggroindicator$preRenderParticle(DeltaTracker deltaTracker,
                                                  boolean renderBlockOutline,
                                                  Camera camera,
                                                  GameRenderer gameRenderer,
                                                  LightTexture lightTexture,
                                                  Matrix4f frustumMatrix,
                                                  Matrix4f projectionMatrix,
                                                  CallbackInfo ci,
                                                  @Local PoseStack poseStack) {
        AlertRenderer.renderAlertIcon(deltaTracker.getRealtimeDeltaTicks(),
                poseStack, camera);
    }
}
