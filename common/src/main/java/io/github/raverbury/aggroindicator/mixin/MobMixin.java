package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.CommonClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Shadow
    private LivingEntity target;

    @Inject(method = "setTarget", at = @At(value = "RETURN"))
    private void aggroindicator$dispatchCLCTEOnChangeTarget(LivingEntity targetArg, CallbackInfo ci) {
        if (((Mob) (Object) this).level().isClientSide) {
            return;
        }
        CommonClass.livingChangeTarget((Mob) (Object) this, target);
    }
}
