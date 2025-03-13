package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.AggroIndicator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobMixin {
    @Shadow
    private LivingEntity target;

    @Inject(method = "setTarget", at = @At(value = "RETURN"))
    private void aggroindicator$dispatchCLCTEOnChangeTarget(LivingEntity targetArg, CallbackInfo ci) {
        if (((MobEntity) (Object) this).getWorld().isClient()) {
            return;
        }
        AggroIndicator.livingChangeTarget((MobEntity) (Object) this, target);
    }
}
