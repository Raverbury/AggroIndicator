package com.github.raverbury.aggroindicator.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;

public interface EntityTickEventCallback {

    Event<EntityTickEventCallback> EVENT = EventFactory.createArrayBacked(
            EntityTickEventCallback.class, (listeners) -> (entity) -> {
                for (EntityTickEventCallback listener : listeners) {
                    ActionResult result = listener.interact(entity);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(Entity entity);
}
