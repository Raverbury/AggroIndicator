package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.AggroIndicator;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "remove", at = @At(value = "HEAD"))
    private void aggroindicator$playerLogOut(ServerPlayerEntity player,
                                             CallbackInfo ci) {
        AggroIndicator.clearMobTargetingThisPlayer(player);
    }
}
