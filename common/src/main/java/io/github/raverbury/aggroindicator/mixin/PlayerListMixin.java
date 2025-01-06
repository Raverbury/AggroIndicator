package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.CommonClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "remove", at = @At(value = "HEAD"))
    private void aggroindicator$playerLogOut(ServerPlayer player, CallbackInfo ci) {
        CommonClass.clearMobTargetingThisPlayer(player);
    }
}
