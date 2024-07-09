package com.github.raverbury.aggroindicator.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class EntityPostTickEvent extends Event {
    private final Entity entity;

    public EntityPostTickEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
