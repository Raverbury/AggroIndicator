package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.PacifyTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacifyTask.class)
public class PacifyTaskMixin {

    /**
     * Dispatch LCT with null since mob is pacified
     *
     * @param memoryQueryResult
     * @param i
     * @param memoryQueryResult2
     * @param world
     * @param entity
     * @param time
     * @param cir
     */
    // @Inject(method = "method_46906", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/MemoryQueryResult;remember(Ljava/lang/Object;J)V"))
    // private static void aggroIndicator$dispatchLCTWithNullWhenPacified(MemoryQueryResult memoryQueryResult, int i, MemoryQueryResult memoryQueryResult2, ServerWorld world, LivingEntity entity, long time, CallbackInfoReturnable<Boolean> cir) {
    //     AggroIndicator.LOGGER.debug("PACIFIED");
    //     LivingChangeTargetCallback.EVENT.invoker()
    //             .interact((MobEntity) entity, null);
    // }
}
