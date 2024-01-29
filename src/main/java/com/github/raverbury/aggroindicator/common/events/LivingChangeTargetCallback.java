package com.github.raverbury.aggroindicator.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public interface LivingChangeTargetCallback {

    Event<LivingChangeTargetCallback> EVENT = EventFactory.createArrayBacked(LivingChangeTargetCallback.class,
            (listeners) -> (mob, livingTarget) -> {
                for (LivingChangeTargetCallback listener : listeners) {
                    ActionResult result = listener.interact(mob, livingTarget);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(MobEntity mob, @Nullable LivingEntity livingTarget);
}
