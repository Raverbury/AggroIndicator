package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.client.AlertRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)" +
            "V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client" +
            "/renderer/GameRenderer;resetData()V", shift = At.Shift.AFTER))
    private void aggroindicator$clientPlayerLogout(Screen nextScreen, boolean keepResourcePacks, CallbackInfo ci) {
        AlertRenderer.clearAggroingMobs();
    }
}