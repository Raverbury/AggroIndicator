package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.AlertRenderer;
import com.github.raverbury.aggroindicator.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ClientEventHandler {

    public static void register() {
        // MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandler::handleRenderLivingEvent);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLevelStageEvent);
    }

    @Deprecated
    public static void handleRenderLivingEvent(RenderLivingEvent<? extends LivingEntity, ? extends EntityModel<?>> event) {
        // AggroIndicator.LOGGER.info(event.isCancelable() ? "is cancelable" : "not cancelable");
        if (event.isCanceled() || !event.getEntity().level().isClientSide()) {
            // AggroIndicator.LOGGER.info("Event is canceled: " + ((event.isCanceled()) ? "Y" : "N"));
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
        if (!ClientConfig.RENDER_ALERT_ICON.get()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return;
        }
        List<Mob> nearbyMobs = level.getNearbyEntities(Mob.class,
                TargetingConditions.forCombat().range(ClientConfig.RENDER_RANGE.get()).ignoreInvisibilityTesting()
                        .ignoreLineOfSight(), player, player.getBoundingBox().inflate(ClientConfig.RENDER_RANGE.get()));
        if (nearbyMobs.isEmpty()) {
            return;
        }
        for (Mob mob: nearbyMobs) {
            if (shouldDrawAlert(mob)) {
                AlertRenderer.addEntity(mob);
            }
        }
        AlertRenderer.renderAlertIcon(event.getPartialTick(), event.getPoseStack(),
                Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public static boolean shouldDrawAlert(LivingEntity clientEntity) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        final boolean TOO_FAR_AWAY = cameraEntity == null || clientEntity.distanceTo(
                cameraEntity) > ClientConfig.RENDER_RANGE.get();
        if (TOO_FAR_AWAY) {
            return false;
        }

        final boolean NOT_A_MOB = !(clientEntity instanceof Mob);
        if (NOT_A_MOB) {
            return false;
        }

        String entityRegistryName = Objects.requireNonNull(
                ForgeRegistries.ENTITY_TYPES.getKey(clientEntity.getType())).toString();
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
            // AggroIndicator.LOGGER.info("Mob is blacklisted");
            return false;
        }

        Player player = Minecraft.getInstance().player;
        final boolean ENTITY_IS_INVISIBLE = player == null || clientEntity.isInvisibleTo(player);
        if (ENTITY_IS_INVISIBLE) {
            return false;
        }

        final boolean IS_TARGETING_CLIENT_PLAYER = AlertRenderer.shouldDrawThisUuid(clientEntity.getUUID());
        if (!IS_TARGETING_CLIENT_PLAYER) {
            // AggroIndicator.LOGGER.info("Mob is not targeting this player");
            return false;
        }

        final boolean PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS = player.hasEffect(
                MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS);
        if (PLAYER_HAS_STATUS_BLINDNESS_OR_DARKNESS) {
            return false;
        }
        // AggroIndicator.LOGGER.debug("Valid render target found, adding to list");

        return true;
    }
}
