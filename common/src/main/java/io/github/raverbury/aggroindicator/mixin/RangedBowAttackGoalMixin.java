package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.CommonClass;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedBowAttackGoal.class)
public class RangedBowAttackGoalMixin<T extends Monster & RangedAttackMob> {

    @Shadow
    @Final
    private T mob;

    @Inject(
            method = "stop",
            at = @At(
                    value = "HEAD"
            )
    )
    private void aggroindicator$setNotAboutToAttack(CallbackInfo ci) {
        CommonClass.livingAboutToAttack(this.mob,
                false);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "RETURN"
            )
    )
    private void aggroindicator$setAboutToAttack(CallbackInfo ci) {
        CommonClass.livingAboutToAttack(this.mob,
                this.mob.isUsingItem());
    }
}
