package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.util.IAggroIndicatorAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalMixin<T extends net.minecraft.world.entity.Mob & RangedAttackMob> extends Goal implements IAggroIndicatorAttackGoal {

    @Shadow
    protected T mob;

    @Shadow
    protected float attackRadiusSqr;

    @Override
    public boolean isAboutToAttack(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return mob.isUsingItem() && (target.distanceToSqr(this.mob) <= this.attackRadiusSqr);
    }
}
