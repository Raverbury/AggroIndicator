package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(UpdateAttackTargetTask.class)
public class UpdateAttackTargetTaskMixin {

    @Inject(at = @At(value = "RETURN"), method = "create(Ljava/util/function" + "/Predicate;Ljava/util/function/Function;)" + "Lnet/minecraft/entity/ai/brain/task/Task;", cancellable = true)
    private static <E extends MobEntity> void create(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter, CallbackInfoReturnable<Task<E>> cir) {
        cir.setReturnValue(TaskTriggerer.task((context) -> {
            return context.group(
                            context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
                            context.queryMemoryOptional(
                                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE))
                    .apply(context,
                            (attackTarget, cantReachWalkTargetSince) -> {
                                return (world, entity, time) -> {
                                    if (!startCondition.test(entity)) {
                                        return false;
                                    } else {
                                        Optional<? extends LivingEntity> optional = targetGetter.apply(
                                                entity);
                                        if (optional.isEmpty()) {
                                            return false;
                                        } else {
                                            LivingEntity livingEntity = (LivingEntity) optional.get();
                                            if (!entity.canTarget(
                                                    livingEntity)) {
                                                return false;
                                            } else {
                                                ActionResult result = LivingChangeTargetCallback.EVENT.invoker()
                                                        .interact(entity,
                                                                livingEntity);
                                                if (result == ActionResult.FAIL) {
                                                    return false;
                                                }
                                                attackTarget.remember(
                                                        livingEntity);
                                                cantReachWalkTargetSince.forget();
                                                return true;
                                            }
                                        }
                                    }
                                };
                            });
        }));
        cir.cancel();
    }
}
