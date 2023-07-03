package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AlertRenderer;
import com.github.raverbury.aggroindicator.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.regex.Pattern;

public class ClientEventHandler {

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
        if (!ClientConfig.RENDER_ALERT_ICON.get()) {
            return false;
        }
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        final boolean TOO_FAR_AWAY = cameraEntity == null || renderedEntity.distanceTo(cameraEntity) > ClientConfig.RENDER_RANGE.get();
        if (TOO_FAR_AWAY) {
            return false;
        }

        final boolean NOT_A_MOB = !(renderedEntity instanceof Mob);
        if (NOT_A_MOB) {
            return false;
        }

        String entityRegistryName = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(renderedEntity.getType())).toString();
        boolean IS_BLACKLISTED = false;
        for (String item : ClientConfig.CLIENT_MOB_BLACKLIST.get()
        ) {
            item = item.replace("*", ".*");
            Pattern pattern = Pattern.compile(item, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(entityRegistryName).matches()) {
                IS_BLACKLISTED = true;
                break;
            }
        }
        if (IS_BLACKLISTED) {
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

        final boolean PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS = player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS);
        if (PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS) {
            return false;
        }
        // AggroIndicator.LOGGER.debug("Valid render target found");

        return true;
    }
}
