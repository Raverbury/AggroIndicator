package io.github.raverbury.aggroindicator.accessors;

import net.minecraft.world.entity.LivingEntity;

public interface BrainAccess {
    public void aggroindicator$setBrainOwner(LivingEntity owner);
    public LivingEntity aggroindicator$getBrainOwner();
}
