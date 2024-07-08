package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(WalkToNearestVisibleWantedItemTask.class)
public class WalkToNearestVisibleWantedItemTaskMixin {
    // @Inject(method = "method_46945", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/MemoryQueryResult;remember(Ljava/lang/Object;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private static <E extends LivingEntity> void aggroIndicator$dispatchLCTWithNullOnSettingWalkTarget(TaskTriggerer.TaskContext taskContext, MemoryQueryResult memoryQueryResult, MemoryQueryResult memoryQueryResult2, Predicate predicate, int i, float f, MemoryQueryResult memoryQueryResult3, MemoryQueryResult memoryQueryResult4, ServerWorld world, LivingEntity entity, long time, CallbackInfoReturnable<Boolean> cir, ItemEntity itemEntity, WalkTarget walkTarget) {
    //     AggroIndicator.LOGGER.debug("WALKING");
    //     try {
    //         Optional<LivingEntity> optionalRegisteredTarget =
    //                 entity.getBrain().getOptionalRegisteredMemory(
    //                         MemoryModuleType.ATTACK_TARGET);
    //         Optional<LivingEntity> optionalTarget =
    //                 entity.getBrain()
    //                         .getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
    //         AggroIndicator.LOGGER.debug(
    //                 "optional reg target: " + optionalRegisteredTarget.orElseGet(
    //                         () -> {
    //                             return null;
    //                         }));
    //         if (optionalTarget != null && optionalTarget.isPresent()) {
    //             AggroIndicator.LOGGER.debug(
    //                     "optional target: " + optionalTarget.orElseGet(() -> {
    //                         return null;
    //                     }));
    //         }
    //     } catch (Exception e) {
    //         AggroIndicator.LOGGER.debug(e.getMessage());
    //     }
    //     LivingChangeTargetCallback.EVENT.invoker()
    //             .interact((MobEntity) entity, null);
    // }
}
