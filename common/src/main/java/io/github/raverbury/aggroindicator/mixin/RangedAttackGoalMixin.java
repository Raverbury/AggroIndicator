package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.CommonClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedAttackGoal.class)
public class RangedAttackGoalMixin {
    @Shadow
    @Final
    private RangedAttackMob rangedAttackMob;

    @Shadow
    private int attackTime;

    @Inject(
            method = "stop",
            at = @At(
                    value = "HEAD"
            )
    )
    private void aggroindicator$setNotAboutToAttack(CallbackInfo ci) {
        CommonClass.livingAboutToAttack((LivingEntity) this.rangedAttackMob,
                false);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "RETURN"
            )
    )
    private void aggroindicator$setAboutToAttack(CallbackInfo ci) {
        CommonClass.livingAboutToAttack((LivingEntity) this.rangedAttackMob,
                this.attackTime > 0 && this.attackTime <= 20);
    }
}
