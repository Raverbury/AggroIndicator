package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.util.IAggroIndicatorAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin extends Goal implements IAggroIndicatorAttackGoal {

    @Shadow
    protected PathfinderMob mob;

    @Shadow
    abstract protected int getTicksUntilNextAttack();

    @Shadow
    abstract protected int getAttackInterval();

    @Shadow
    abstract protected double getAttackReachSqr(LivingEntity target);

    @Override
    public boolean isAboutToAttack(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return (this.getTicksUntilNextAttack() <= 20) && (target.distanceToSqr(this.mob) <= this.getAttackReachSqr(target) + (4f * target.getBbWidth()));
//        return (target.distanceTo(this.mob) <= this.getAttackReachSqr(target));
    }
}
