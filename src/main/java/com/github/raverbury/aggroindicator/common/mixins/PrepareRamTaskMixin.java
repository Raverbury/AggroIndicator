package com.github.raverbury.aggroindicator.common.mixins;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
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
public class PrepareRamTaskMixin<E extends PathAwareEntity> {
    @Shadow
    private Optional<PrepareRamTask.Ram> ram;

    /**
     * Dispatch LTC with null because here, status 59 is sent
     *
     * @param serverWorld
     * @param pathAwareEntity
     * @param l
     * @param ci
     */
    @Inject(method = "finishRunning" + "(Lnet/minecraft/server/world" + "/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world" + "/ServerWorld;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
    private void aggroIndicator$dispatchLCTWithNullOnRunDone(ServerWorld serverWorld, E pathAwareEntity, long l, CallbackInfo ci) {
        // according to GoatEntity#handleStatus and GoatEntity#tickMovement
        // if status 59 = raise head pitch
        // status 58 = lower head pitch
        // these can be interpreted as begin/stop ram signals
        LivingChangeTargetCallback.EVENT.invoker()
                .interact(pathAwareEntity, null);
    }

    /**
     * Dispatch LTC with null because here, status 59 is sent
     *
     * @param serverWorld
     * @param pathAwareEntity
     * @param l
     * @param ci
     */
    @Inject(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V", ordinal = 0))
    private void aggroIndicator$dispatchLCTWithNullTargetIfInvalid(ServerWorld serverWorld, E pathAwareEntity, long l, CallbackInfo ci) {
        LivingChangeTargetCallback.EVENT.invoker()
                .interact(pathAwareEntity, null);
    }

    /**
     * Dispatch LTC with valid ram target because here, status 58 is sent
     *
     * @param serverWorld
     * @param pathAwareEntity
     * @param l
     * @param ci
     */
    @Inject(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V", ordinal = 1))
    private void aggroIndicator$dispatchLCTWithRamTargetIfValid(ServerWorld serverWorld, E pathAwareEntity, long l, CallbackInfo ci) {
        LivingChangeTargetCallback.EVENT.invoker()
                .interact(pathAwareEntity, this.ram.get().getEntity());
    }

}


