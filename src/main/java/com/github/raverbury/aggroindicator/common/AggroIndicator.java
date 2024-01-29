package com.github.raverbury.aggroindicator.common;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggroIndicator implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("aggroindicator");
    public static final String MODID = "aggroindicator";

    @Override
    public void onInitialize() {
        registerCommonEventHandlers();
    }

    public void registerCommonEventHandlers() {
        LivingChangeTargetCallback.EVENT.register((mob, newTarget) -> {
            if (mob.getWorld().isClient) {
                return ActionResult.PASS;
            }

            @Nullable LivingEntity oldTarget = mob.getTarget();
            if (newTarget == oldTarget) {
                return ActionResult.PASS;
            }

            LOGGER.info(mob.getName().getString() + " switches target from " +
                    (oldTarget != null ? oldTarget.getName().getString() : "null") + " to " +
                    (newTarget != null ? newTarget.getName().getString() : "null"));

            // TODO: send packet

            return ActionResult.PASS;
        });
    }
}