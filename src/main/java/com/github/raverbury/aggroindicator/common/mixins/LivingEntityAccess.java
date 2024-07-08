package com.github.raverbury.aggroindicator.common.mixins;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccess {
    /**
     * Retrieve LivingEntity#dead variable
     *
     * @return The LivingEntity#dead variable
     */
    @Accessor
    boolean getDead();
}
