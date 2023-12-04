package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.util.IAggroIndicatorAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RangedCrossbowAttackGoal.class)
public abstract class RangedCrossbowAttackGoalMixin<T extends Monster & RangedAttackMob
        & CrossbowAttackMob> extends Goal implements IAggroIndicatorAttackGoal {

    @Shadow
    protected T mob;

    @Shadow
    protected float attackRadiusSqr;

    @Shadow
    private int attackDelay;

    @Override
    public boolean isAboutToAttack(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return (attackDelay > 0) && (attackDelay <= 20);
    }
}
