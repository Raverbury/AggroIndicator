package com.github.raverbury.aggroindicator.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

public class CustomLivingChangeTargetEvent extends LivingEvent {
    private final LivingEntity target;

    public CustomLivingChangeTargetEvent(LivingEntity entity,
                                         LivingEntity target) {
        super(entity);
        this.target = target;
    }

    public LivingEntity getTarget() {
        return this.target;
    }
}
