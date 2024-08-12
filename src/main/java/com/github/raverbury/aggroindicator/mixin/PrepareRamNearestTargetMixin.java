package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.event.CustomLivingChangeTargetEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PrepareRamNearestTarget.class)
public abstract class PrepareRamNearestTargetMixin {
    @Shadow
    private Optional<PrepareRamNearestTarget.RamCandidate> ramCandidate;

    @Inject(method = "stop(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private <E extends PathfinderMob> void aggroIndicator$goatStopRam(ServerLevel serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
                new CustomLivingChangeTargetEvent(pathAwareEntity, null));
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private <E extends PathfinderMob> void aggroindicator$goatLoseTarget(ServerLevel serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
                new CustomLivingChangeTargetEvent(pathAwareEntity, null));
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;" + "Lnet/minecraft/world/entity/PathfinderMob;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private <E extends PathfinderMob> void aggroindicator$goatGainRamTarget(ServerLevel serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
                new CustomLivingChangeTargetEvent(pathAwareEntity,
                        this.ramCandidate.get().getTarget()));
    }
}
