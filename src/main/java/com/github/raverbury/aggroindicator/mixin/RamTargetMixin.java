package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.event.ServerEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RamTarget.class)
public class RamTargetMixin {
    @Inject(method = "finishRam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private void aggroindicator$goatFinishRam(ServerLevel serverLevel,
                                              Goat goat, CallbackInfo ci) {
        ServerEventHandler.processAggroChange(goat, null);
    }
}
