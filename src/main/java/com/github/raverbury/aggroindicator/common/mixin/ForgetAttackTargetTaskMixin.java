package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Mixin(ForgetAttackTargetTask.class)
public class ForgetAttackTargetTaskMixin {
    /**
     * Dispatch LCT with null here because mob is supposed to forget target
     *
     * @param taskContext
     * @param memoryQueryResult
     * @param bl
     * @param memoryQueryResult2
     * @param predicate
     * @param biConsumer
     * @param world
     * @param entity
     * @param time
     * @param cir
     * @param livingEntity
     */
    // @Inject(method = "method_47135", at = @At(value = "INVOKE", target =
    //         "Lnet/minecraft/entity/ai/brain/MemoryQueryResult;forget()V"),
    //         locals = LocalCapture.CAPTURE_FAILSOFT)
    // private static void aggroIndicator$dispatchLCTWithNullOnForget(TaskTriggerer.TaskContext taskContext, MemoryQueryResult memoryQueryResult, boolean bl, MemoryQueryResult memoryQueryResult2, Predicate predicate, BiConsumer biConsumer, ServerWorld world, MobEntity entity, long time, CallbackInfoReturnable<Boolean> cir, LivingEntity livingEntity) {
    //     AggroIndicator.LOGGER.debug("UH WHAT FORGET PLS");
    //     LivingChangeTargetCallback.EVENT.invoker().interact(entity, null);
    // }
}
