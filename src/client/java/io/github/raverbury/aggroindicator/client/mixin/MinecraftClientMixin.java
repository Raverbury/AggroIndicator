package io.github.raverbury.aggroindicator.client.mixin;

import io.github.raverbury.aggroindicator.client.AlertRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;reset()V", shift = At.Shift.AFTER))
    private void aggroindicator$clientPlayerLogout(Screen nextScreen, CallbackInfo ci) {
        AlertRenderer.clearAggroingMobs();
    }
}
