package io.github.raverbury.aggroindicator.accessor;

import net.minecraft.entity.LivingEntity;

public interface BrainAccessor {
    public void aggroindicator$setBrainOwner(LivingEntity entity);

    public LivingEntity aggroindicator$getBrainOwner();
}
