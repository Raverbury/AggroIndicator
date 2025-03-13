package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.AggroIndicator;
import net.minecraft.entity.ai.brain.task.PrepareRamTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PrepareRamTask.class)
public abstract class PrepareRamTaskMixin {

    @Shadow
    private Optional<PrepareRamTask.Ram> ram;

    @Inject(method = "finishRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/mob/PathAwareEntity;J)V", at = @At(value =
            "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
    private <E extends PathAwareEntity> void aggroindicator$goatStopRam(ServerWorld serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        AggroIndicator.livingChangeTarget(pathAwareEntity, null);
    }

    @Inject(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/mob/PathAwareEntity;J)V", at = @At(value =
            "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;" +
            "sendEntityStatus(Lnet/minecraft/entity/Entity;B)V", ordinal = 0))
    private <E extends PathAwareEntity> void aggroindicator$goatLoseTarget(ServerWorld serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        AggroIndicator.livingChangeTarget(pathAwareEntity, null);
    }

    @Inject(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/mob/PathAwareEntity;J)V", at = @At(value =
            "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;" +
            "sendEntityStatus(Lnet/minecraft/entity/Entity;B)V", ordinal = 1))
    private <E extends PathAwareEntity> void aggroindicator$goatGainRamTarget(ServerWorld serverLevel, E pathAwareEntity, long l, CallbackInfo ci) {
        AggroIndicator.livingChangeTarget(pathAwareEntity,
                this.ram.get().getEntity());
    }
}
