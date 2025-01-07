package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.event.CustomLivingChangeTargetEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RamTarget.class)
public class RamTargetMixin {
    @Inject(method = "finishRam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private <E extends PathfinderMob> void aggroindicator$goatFinishRam(ServerLevel p_147835_,
                                                                        E goat,
                                                                        CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
                new CustomLivingChangeTargetEvent(goat, null));
    }
}
