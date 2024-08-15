package io.github.raverbury.aggroindicator.common.mixins;

import io.github.raverbury.aggroindicator.common.events.CustomLivingChangeTargetEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.animal.goat.Goat;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RamTarget.class)
public class RamTargetMixin {
    @Inject(method = "finishRam(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/goat/Goat;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private void aggroindicator$goatFinishRam(ServerLevel level, Goat goat,
                                              CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new CustomLivingChangeTargetEvent(goat, null));
    }
}
