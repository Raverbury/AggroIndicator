package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.CommonClass;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    /**
     * Mixin to recreate OnLevelUnload in Neo/Forge
     * @param ci
     */
    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;close()V"))
    private void aggroindicator$clearMobTargetPlayerMapOnLevelUnload(CallbackInfo ci) {
        CommonClass.clearMobTargetPlayerMap();
    }
}
