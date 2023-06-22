package com.github.raverbury.aggroindicator.events;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// @Mod.EventBusSubscriber(modid = AggroIndicator.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ServerEventHandler {

    private static final float DISTANCE = 32f;

    // @SubscribeEvent
    public static void handleRenderLivingEvent(LivingEvent.LivingTickEvent event) {
        AggroIndicator.LOGGER.debug("RLE register?");
        if (event.isCanceled()) {
            return;
        }
        if (!shouldDrawAlert(event.getEntity())) {
            return;
        }
        AlertRenderer.addEntity(event.getEntity());
    }

    public static boolean shouldDrawAlert(LivingEntity tickedEntity) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        final boolean TOO_FAR_AWAY = cameraEntity == null || tickedEntity.distanceTo(cameraEntity) > DISTANCE;
        if (TOO_FAR_AWAY) {
            AggroIndicator.LOGGER.debug("too far");
            return false;
        }

        final boolean NOT_A_MOB = !(tickedEntity instanceof Mob);
        if (NOT_A_MOB) {
            AggroIndicator.LOGGER.debug("not a mob");
            return false;
        }

        Mob mob = (Mob) tickedEntity;
        LivingEntity target = mob.getTarget();
        final boolean NO_ATTACK_TARGET = target == null;
        if (NO_ATTACK_TARGET) {
            AggroIndicator.LOGGER.debug("no target");
            AggroIndicator.LOGGER.debug(mob.toString());
            AggroIndicator.LOGGER.debug(mob.getMobType().toString());
            AggroIndicator.LOGGER.debug(mob.getTarget() != null? mob.getTarget().getName().getString() : "No target");
            return false;
        }

        Player player = Minecraft.getInstance().player;
        final boolean TARGET_IS_NOT_PLAYER = (LivingEntity) player == null || !(target.is((LivingEntity) player));
        if (TARGET_IS_NOT_PLAYER) {
            AggroIndicator.LOGGER.debug("not player");
            return false;
        }

        final boolean ENTITY_IS_INVISIBLE = tickedEntity.isInvisibleTo(player);
        if (ENTITY_IS_INVISIBLE) {
            AggroIndicator.LOGGER.debug("invisible");
            return false;
        }

        AggroIndicator.LOGGER.debug("Got a valid entity here");
        return true;
    }
}
