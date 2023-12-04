package com.github.raverbury.aggroindicator.util;

import net.minecraft.world.entity.LivingEntity;

public interface IAggroIndicatorAttackGoal {
    public boolean isAboutToAttack(LivingEntity target);
}
