package io.github.raverbury.aggroindicator.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.List;
import java.util.regex.Pattern;

public class ClientEventHandler {

    public static void register(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::handleWorldUnloadEvent);
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::handleRenderLevelStageEvent);
        eventBus.addListener(ClientEventHandler::handleConfigEvent);
    }

    public static void handleConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == ClientConfig.SPEC) {
            ClientConfig.reloadCache();
            AlertRenderer.reloadAggroIcon();
        }
    }

    public static void handleWorldUnloadEvent(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        AlertRenderer.clearAggroingMobs();
    }

    public static void handleRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        if (!ClientConfig.renderAlertIcon) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return;
        }
        List<Mob> nearbyMobs = level.getNearbyEntities(Mob.class,
                TargetingConditions.forCombat().range(ClientConfig.renderRange)
                        .ignoreInvisibilityTesting().ignoreLineOfSight(),
                player,
                player.getBoundingBox().inflate(ClientConfig.renderRange));
        if (nearbyMobs.isEmpty()) {
            return;
        }
        for (Mob mob : nearbyMobs) {
            if (shouldDrawAlert(mob)) {
                AlertRenderer.addEntity(mob);
            }
        }
        AlertRenderer.renderAlertIcon(
                event.getPartialTick().getRealtimeDeltaTicks(),
                event.getPoseStack(),
                Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public static boolean shouldDrawAlert(LivingEntity clientEntity) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        final boolean TOO_FAR_AWAY = cameraEntity == null || clientEntity.distanceTo(
                cameraEntity) > ClientConfig.renderRange;
        if (TOO_FAR_AWAY) {
            return false;
        }

        final boolean NOT_A_MOB = !(clientEntity instanceof Mob);
        if (NOT_A_MOB) {
            return false;
        }

        String entityRegistryName = BuiltInRegistries.ENTITY_TYPE.getKey(
                clientEntity.getType()).toString();
        boolean IS_BLACKLISTED = false;
        for (String item : ClientConfig.clientMobBlacklist) {
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
        final boolean ENTITY_IS_INVISIBLE = player == null || clientEntity.isInvisibleTo(
                player);
        if (ENTITY_IS_INVISIBLE) {
            return false;
        }

        final boolean IS_TARGETING_CLIENT_PLAYER = AlertRenderer.shouldDrawThisUuid(
                clientEntity.getUUID());
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
