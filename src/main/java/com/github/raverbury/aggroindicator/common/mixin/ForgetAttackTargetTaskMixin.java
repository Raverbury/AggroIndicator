package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Mixin(ForgetAttackTargetTask.class)
public class ForgetAttackTargetTaskMixin {
    @Inject(at = @At(value = "RETURN"), method = "create(Ljava/util/function" + "/Predicate;Ljava/util/function/BiConsumer;Z)" + "Lnet/minecraft/entity/ai/brain/task/Task;", cancellable = true)
    private static <E extends MobEntity> void create(Predicate<LivingEntity> alternativeCondition, BiConsumer<E, LivingEntity> forgetCallback, boolean shouldForgetIfTargetUnreachable, CallbackInfoReturnable<Task<E>> cir) {
        cir.setReturnValue(TaskTriggerer.task((context) -> {
            return context.group(
                            context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET),
                            context.queryMemoryOptional(
                                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE))
                    .apply(context,
                            (attackTarget, cantReachWalkTargetSince) -> {
                                return (world, entity, time) -> {
                                    LivingEntity livingEntity = (LivingEntity) context.getValue(
                                            attackTarget);
                                    if (entity.canTarget(
                                            livingEntity) && (!shouldForgetIfTargetUnreachable || !cannotReachTarget(
                                            entity, context.getOptionalValue(
                                                    cantReachWalkTargetSince))) && livingEntity.isAlive() && livingEntity.getWorld() == entity.getWorld() && !alternativeCondition.test(
                                            livingEntity)) {
                                        return true;
                                    } else {
                                        forgetCallback.accept(entity,
                                                livingEntity);
                                        attackTarget.forget();
                                        ActionResult result = LivingChangeTargetCallback.EVENT.invoker()
                                                .interact(entity, null);
                                        return result != ActionResult.FAIL;
                                    }
                                };
                            });
        }));
        cir.cancel();
    }

    @Shadow
    private static boolean cannotReachTarget(LivingEntity livingEntity, Optional<Long> optional) {
        throw new AssertionError();
    }
}
