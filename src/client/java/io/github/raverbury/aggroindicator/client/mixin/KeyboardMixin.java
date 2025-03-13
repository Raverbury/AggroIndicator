package io.github.raverbury.aggroindicator.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.raverbury.aggroindicator.client.config.ClientConfig;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @WrapOperation(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;openPauseMenu(Z)V")
    )
    private void aggroindicator$reloadConfigOnPause(MinecraftClient instance, boolean pause, Operation<Void> original) {
        ClientConfig.loadOrDefault();
        original.call(instance, pause);
    }
}
