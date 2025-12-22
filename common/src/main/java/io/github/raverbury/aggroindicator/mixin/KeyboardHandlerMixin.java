package io.github.raverbury.aggroindicator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.raverbury.aggroindicator.ClientConfig;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @WrapOperation(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;pauseGame(Z)V")
    )
    private void aggroindicator$reloadConfigOnPause(Minecraft instance, boolean b, Operation<Void> original) {
        ClientConfig.loadOrDefault();
        original.call(instance, b);
    }
}
