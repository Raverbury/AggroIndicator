package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(UpdateAttackTargetTask.class)
public class UpdateAttackTargetTaskMixin {
    /**
     * Dispatch LCT with attack target
     *
     * @param predicate
     * @param function
     * @param memoryQueryResult
     * @param memoryQueryResult2
     * @param world
     * @param entity
     * @param time
     * @param cir
     * @param optional
     * @param livingEntity
     * @param <E>
     */
    // @Inject(method = "method_47123(Ljava/util/function/Predicate;" + "Ljava" + "/util/function/Function;" + "Lnet/minecraft/entity/ai/brain" + "/MemoryQueryResult;" + "Lnet/minecraft/entity/ai/brain" + "/MemoryQueryResult;" + "Lnet/minecraft/server/world/ServerWorld;" + "Lnet/minecraft/entity/mob/MobEntity;J)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai" + "/brain/MemoryQueryResult;remember(Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private static <E extends MobEntity> void aggroIndicator$dispatchLCTWithAttackTargetWhenRemember(Predicate predicate, Function function, MemoryQueryResult memoryQueryResult, MemoryQueryResult memoryQueryResult2, ServerWorld world, MobEntity entity, long time, CallbackInfoReturnable<Boolean> cir, Optional optional, LivingEntity livingEntity) {
    //     AggroIndicator.LOGGER.debug("update attack target");
    //     LivingChangeTargetCallback.EVENT.invoker()
    //             .interact(entity, livingEntity);
    // }
}
