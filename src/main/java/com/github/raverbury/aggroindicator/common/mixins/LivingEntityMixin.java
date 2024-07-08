package com.github.raverbury.aggroindicator.common.mixins;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    /**
     * Dispatch LCT with null on death to clear this mob's entry in client
     * aggroing list
     *
     * @param damageSource
     * @param ci
     */
    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    private void aggroIndicator$dispatchLCTWithNullOnDeath(DamageSource damageSource, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            LivingChangeTargetCallback.EVENT.invoker()
                    .interact((MobEntity) (Object) this, null);
        }
    }
}
