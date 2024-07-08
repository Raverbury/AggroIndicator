package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    // @Inject(method = "setAdmiringItem", at = @At(value = "RETURN"))
    // private static void aggroIndicator$dispatchLCTWithNullOnAdmiring(LivingEntity entity, CallbackInfo ci) {
    //     LivingChangeTargetCallback.EVENT.invoker()
    //             .interact((MobEntity) entity, null);
    // }
}
