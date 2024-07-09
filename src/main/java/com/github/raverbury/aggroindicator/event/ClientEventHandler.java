package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AlertRenderer;
import com.github.raverbury.aggroindicator.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ClientEventHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(
                ClientEventHandler::handleWorldUnloadEvent);
        MinecraftForge.EVENT_BUS.addListener(
                ClientEventHandler::handleRenderLevelStageEvent);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientEventHandler::handleConfigEvent);
    }

    public static void handleConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == ClientConfig.INSTANCE) {
            ClientConfig.Cached.reload();
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
        if (event.isCanceled()) {
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        if (!ClientConfig.Cached.RENDER_ALERT_ICON) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return;
        }
        List<Mob> nearbyMobs = level.getNearbyEntities(Mob.class,
                TargetingConditions.forCombat()
                        .range(ClientConfig.Cached.RENDER_RANGE)
                        .ignoreInvisibilityTesting().ignoreLineOfSight(),
                player, player.getBoundingBox()
                        .inflate(ClientConfig.Cached.RENDER_RANGE));
        if (nearbyMobs.isEmpty()) {
            return;
        }
        for (Mob mob : nearbyMobs) {
            if (shouldDrawAlert(mob)) {
                AlertRenderer.addEntity(mob);
            }
        }
        AlertRenderer.renderAlertIcon(event.getPartialTick(),
                event.getPoseStack(),
                Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public static boolean shouldDrawAlert(LivingEntity clientEntity) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Entity cameraEntity = minecraftClient.getCameraEntity();
        final boolean TOO_FAR_AWAY = cameraEntity == null || clientEntity.distanceTo(
                cameraEntity) > ClientConfig.Cached.RENDER_RANGE;
        if (TOO_FAR_AWAY) {
            return false;
        }

        final boolean NOT_A_MOB = !(clientEntity instanceof Mob);
        if (NOT_A_MOB) {
            return false;
        }

        String entityRegistryName = Objects.requireNonNull(
                        ForgeRegistries.ENTITY_TYPES.getKey(clientEntity.getType()))
                .toString();
        boolean IS_BLACKLISTED = false;
        for (String item : ClientConfig.Cached.CLIENT_MOB_BLACKLIST) {
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
