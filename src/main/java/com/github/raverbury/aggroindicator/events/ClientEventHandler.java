package com.github.raverbury.aggroindicator.events;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientEventHandler {

    private static final float DISTANCE = 32f;

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLivingEvent);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLevelStageEvent);
    }

    public static void handleRenderLivingEvent(RenderLivingEvent.Post<? extends LivingEntity, ? extends EntityModel<?>> event) {
        if (event.isCanceled() || !event.getEntity().level.isClientSide()) {
            return;
        }
        if (!shouldDrawAlert(event.getEntity())) {
            return;
        }
        AlertRenderer.addEntity(event.getEntity());
    }

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
            return false;
        }

        final boolean NOT_A_MOB = !(renderedEntity instanceof Mob);
        if (NOT_A_MOB) {
            return false;
        }

        Player player = Minecraft.getInstance().player;
        final boolean ENTITY_IS_INVISIBLE = player == null || renderedEntity.isInvisibleTo(player);
        if (ENTITY_IS_INVISIBLE) {
            return false;
        }

        final boolean IS_TARGETING_CLIENT_PLAYER = AlertRenderer.shouldDrawThisUuid(renderedEntity.getUUID());
        if (!IS_TARGETING_CLIENT_PLAYER) {
            // AggroIndicator.LOGGER.debug("Final check failed");
            return false;
        }
        // AggroIndicator.LOGGER.debug("Valid render target found");

        return true;
    }
}
