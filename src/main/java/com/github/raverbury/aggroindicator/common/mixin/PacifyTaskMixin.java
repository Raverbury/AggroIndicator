package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.PacifyTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacifyTask.class)
public class PacifyTaskMixin {

    @Inject(at = @At(value = "RETURN"), method = "create", cancellable = true)
    private static void create(MemoryModuleType<?> requiredMemory, int duration, CallbackInfoReturnable<Task<LivingEntity>> cir) {
        cir.setReturnValue(TaskTriggerer.task((context) -> {
            return context.group(
                            context.queryMemoryOptional(MemoryModuleType.ATTACK_TARGET),
                            context.queryMemoryAbsent(MemoryModuleType.PACIFIED),
                            context.queryMemoryValue(requiredMemory))
                    .apply(context, context.supply(() -> {
                        return "[BecomePassive if " + requiredMemory + " present]";
                    }, (attackTarget, pacified, requiredMemoryResult) -> {
                        return (world, entity, time) -> {
                            pacified.remember(true, (long) duration);
                            attackTarget.forget();
                            if (entity instanceof MobEntity mobEntity) {
                                ActionResult result = LivingChangeTargetCallback.EVENT.invoker()
                                        .interact(mobEntity, null);
                                return result != ActionResult.FAIL;
                            }
                            return true;
                        };
                    }));
        }));
        cir.cancel();
    }
}
