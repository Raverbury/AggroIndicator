package io.github.raverbury.aggroindicator.mixins;

import io.github.raverbury.aggroindicator.common.CommonEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PrepareRamNearestTarget.class)
public class PrepareRamNearestTargetMixin {

    @Shadow
    private Optional<PrepareRamNearestTarget.RamCandidate> ramCandidate;

    @Inject(method = "stop(Lnet/minecraft/server/level/ServerLevel;" +
            "Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value =
            "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private <E extends PathfinderMob> void aggroIndicator$goatStopRam(ServerLevel serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        CommonEventHandler.processAggroChange(pathAwareEntity, null);
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 0))
    private <E extends PathfinderMob> void aggroindicator$goatLoseTarget(ServerLevel serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        CommonEventHandler.processAggroChange(pathAwareEntity, null);
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 1))
    private <E extends PathfinderMob> void aggroindicator$goatGainRamTarget(ServerLevel serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        CommonEventHandler.processAggroChange(
                pathAwareEntity, this.ramCandidate.get().getTarget());
    }
}
