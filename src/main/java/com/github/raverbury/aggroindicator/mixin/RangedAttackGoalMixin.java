package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.util.IAggroIndicatorAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RangedAttackGoal.class)
public abstract class RangedAttackGoalMixin extends Goal implements IAggroIndicatorAttackGoal {

    @Shadow
    protected Mob mob;

    @Shadow
    protected int attackTime;

    @Shadow
    protected float attackRadiusSqr;

    @Override
    public boolean isAboutToAttack(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return (attackTime > 0 && attackTime <= 20) && (target.distanceToSqr(this.mob) <= this.attackRadiusSqr);
    }
}
