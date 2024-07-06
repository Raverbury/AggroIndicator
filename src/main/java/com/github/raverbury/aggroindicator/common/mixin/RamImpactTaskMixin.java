package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.ai.brain.task.RamImpactTask;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// I'm no OOP expert, but it's kind of funny that
// for PrepareRamTask they intend for it to be for anything that is a
// PathAwareEntity but for RamImpactTask they said lol no GoatEntity only
// when the 2 are practically connected

/**
 * Check PrepareRamTargetMixin for explanation
 */
@Mixin(RamImpactTask.class)
public class RamImpactTaskMixin {

    @Inject(at = @At(value = "RETURN"), method = "finishRam")
    private void finishRam(ServerWorld world, GoatEntity goat, CallbackInfo ci) {
        // status 59 is sent here, so we dispatch LCT with null
        LivingChangeTargetCallback.EVENT.invoker()
                .interact(goat, null);
    }

}
