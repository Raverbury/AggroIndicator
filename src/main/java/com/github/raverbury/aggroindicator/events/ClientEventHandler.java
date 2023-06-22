package com.github.raverbury.aggroindicator.events;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AggroIndicator.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    private static final float DISTANCE = 32f;

    @SubscribeEvent
    public static void handleRenderLivingEvent(RenderLivingEvent.Post<? extends LivingEntity, ? extends EntityModel<?>> event) {
        AggroIndicator.LOGGER.debug("RLE register?");
        if (event.isCanceled()) {
            return;
        }
        if (!shouldDrawAlert(event.getEntity())) {
            return;
        }
        AlertRenderer.addEntity(event.getEntity());
    }

    @SubscribeEvent
    public static void handleRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        AlertRenderer.renderAlertIcon(event.getPartialTick(), event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public static boolean shouldDrawAlert(LivingEntity renderedEntity) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        final boolean TOO_FAR_AWAY = cameraEntity == null || renderedEntity.distanceTo(cameraEntity) > DISTANCE;
        if (TOO_FAR_AWAY) {
            AggroIndicator.LOGGER.debug("too far");
            return false;
        }

        final boolean NOT_A_MOB = !(renderedEntity instanceof Mob);
        if (NOT_A_MOB) {
            AggroIndicator.LOGGER.debug("not a mob");
            return false;
        }
        return true;

        // Mob mob = (Mob) renderedEntity;
        // LivingEntity target = mob.getTarget();
        // final boolean NO_ATTACK_TARGET = target == null;
        // if (NO_ATTACK_TARGET) {
        //     AggroIndicator.LOGGER.debug("no target");
        //     AggroIndicator.LOGGER.debug(mob.toString());
        //     AggroIndicator.LOGGER.debug(mob.getMobType().toString());
        //     AggroIndicator.LOGGER.debug(mob.getTarget() != null? mob.getTarget().getName().getString() : "No target");
        //     return false;
        // }
        //
        // Player player = Minecraft.getInstance().player;
        // final boolean TARGET_IS_NOT_PLAYER = (LivingEntity) player == null || !(target.is((LivingEntity) player));
        // if (TARGET_IS_NOT_PLAYER) {
        //     AggroIndicator.LOGGER.debug("not player");
        //     return false;
        // }
        //
        // final boolean ENTITY_IS_INVISIBLE = renderedEntity.isInvisibleTo(player);
        // if (ENTITY_IS_INVISIBLE) {
        //     AggroIndicator.LOGGER.debug("invisible");
        //     return false;
        // }
        //
        // AggroIndicator.LOGGER.debug("Got a valid entity here");
        // return true;
    }
}
