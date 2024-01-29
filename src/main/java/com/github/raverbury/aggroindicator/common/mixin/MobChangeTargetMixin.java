package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobChangeTargetMixin {

    @Inject(at = @At(value = "HEAD"), method = "setTarget", cancellable = true)
    private void setTarget(@Nullable LivingEntity target, CallbackInfo ci) {
        ActionResult result = LivingChangeTargetCallback.EVENT.invoker().interact((MobEntity) (Object) this, target);

        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
