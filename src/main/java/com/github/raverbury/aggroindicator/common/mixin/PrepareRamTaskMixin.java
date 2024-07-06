package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.PrepareRamTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
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

    @Inject(at = @At(value = "RETURN"), method = "finishRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V")
    private void finishRunning(ServerWorld serverWorld, E pathAwareEntity, long l, CallbackInfo ci) {
        // copy 1:1 from target method
        // according to GoatEntity#handleStatus and GoatEntity#tickMovement
        // if status 59 = raise head pitch
        // status 58 = lower head pitch
        // these can be interpreted as begin/stop ram signals
        // so I'm injecting my LivingChangeTarget event here
        Brain<?> brain = pathAwareEntity.getBrain();
        if (!brain.hasMemoryModule(MemoryModuleType.RAM_TARGET)) {
            // 59 is sent here, so we dispatch LCT with null
            ActionResult result = LivingChangeTargetCallback.EVENT.invoker()
                    .interact(pathAwareEntity, null);
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" + "Lnet/minecraft/entity/mob/PathAwareEntity;J)V")
    private void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l, CallbackInfo ci) {
        // likewise, copy 1:1 from target method, removing side effects and
        // retaining only the conditional stuff to check whether 58 or 59
        // is set as status
        if (!this.ram.isEmpty()) {
            boolean bl = !((PrepareRamTask.Ram) this.ram.get()).getEntity()
                    .getBlockPos()
                    .equals(((PrepareRamTask.Ram) this.ram.get()).getEnd());
            if (bl) {
                // 59 is sent here, so we dispatch LCT with null
                ActionResult result = LivingChangeTargetCallback.EVENT.invoker()
                        .interact(pathAwareEntity, null);
            } else {
                BlockPos blockPos = pathAwareEntity.getBlockPos();
                if (blockPos.equals(
                        ((PrepareRamTask.Ram) this.ram.get()).getStart())) {
                    // 58 is sent here, so we dispatch LCT with ram target
                    ActionResult result = LivingChangeTargetCallback.EVENT.invoker()
                            .interact(pathAwareEntity,
                                    this.ram.get().getEntity());

                }
            }

        }
    }
}
