package com.github.raverbury.aggroindicator.common;

import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import com.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggroIndicator implements ModInitializer {

    public static final String MOD_ID = "aggroindicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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

            // no change in target then do nothing
            if (newTarget == oldTarget) {
                return ActionResult.PASS;
            }

            // LOGGER.info(mob.getName().getString() + " switches target from " +
            //         (oldTarget != null ? oldTarget.getName().getString() : "null") + " to " +
            //         (newTarget != null ? newTarget.getName().getString() : "null"));

            // old target is player then send packet to that player with playerIsNewTarget = false
            if (oldTarget instanceof PlayerEntity) {
                S2CMobChangeTargetPacket packet1 = new S2CMobChangeTargetPacket(mob.getUuid(), false);
                ServerPlayNetworking.send((ServerPlayerEntity) oldTarget, packet1);
            }

            // new target is player then send packet to that player with playerIsNewTarget = true
            if (newTarget instanceof PlayerEntity) {
                S2CMobChangeTargetPacket packet1 = new S2CMobChangeTargetPacket(mob.getUuid(), true);
                ServerPlayNetworking.send((ServerPlayerEntity) newTarget, packet1);
            }

            return ActionResult.PASS;
        });
    }
}