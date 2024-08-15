package io.github.raverbury.aggroindicator.common.events;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class CustomLivingChangeTargetEvent extends LivingEvent {

    private final LivingEntity target;

    public CustomLivingChangeTargetEvent(LivingEntity entity, LivingEntity target) {
        super(entity);
        this.target = target;
    }

    public LivingEntity getTarget() {
        return this.target;
    }
}
