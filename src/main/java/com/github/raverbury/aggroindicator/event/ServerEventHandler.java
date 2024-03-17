package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

public class ServerEventHandler {

    public static void register(@NotNull IEventBus eventBus) {
        eventBus.addListener(ServerEventHandler::handleLivingChangeTargetEvent);
        eventBus.addListener(ServerEventHandler::handleLivingDeathEvent);
    }

    public static void handleLivingChangeTargetEvent(@NotNull LivingChangeTargetEvent event) {
        // if (getCurrentTarget(event.getEntity()) != event.getNewTarget()) {
        // AggroIndicator.LOGGER.info("LCTE fired with diff: " + ((getCurrentTarget(event.getEntity()) != null)? getCurrentTarget(event.getEntity()).getName() : "no og target") + ((event.getNewTarget() != null)? event.getNewTarget().getName() : "no new target"));
        // }
        if (event.isCanceled() || event.getEntity() == null || event.getEntity().level().isClientSide()) {
            return;
        }
        if (shouldSendDeAggroPacket(event)) {
            PacketDistributor.PLAYER.with((ServerPlayer) getCurrentTarget(event.getEntity())).send(
                    new S2CMobChangeTargetPacket(event.getEntity().getUUID(), false));
            // AggroIndicator.LOGGER.info("Should send deaggro packet");
        }
        if (shouldSendAggroPacket(event)) {
            PacketDistributor.PLAYER.with((ServerPlayer) event.getNewTarget()).send(
                    new S2CMobChangeTargetPacket(event.getEntity().getUUID(), true));
            // AggroIndicator.LOGGER.info("Should send aggro packet");
        }
    }

    public static void handleLivingDeathEvent(@NotNull LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity() == null || event.getEntity().level().isClientSide()) {
            return;
        }
        if (shouldSendDeAggroPacket(event)) {
            //            AggroIndicator.LOGGER.debug("Should send death deaggro packet");
            ServerPlayer serverPlayer = (ServerPlayer) ((Mob) event.getEntity()).getTarget();
            PacketDistributor.PLAYER.with((ServerPlayer) getCurrentTarget(event.getEntity())).send(
                    new S2CMobChangeTargetPacket(event.getEntity().getUUID(), false));
        }
    }

    private static boolean shouldSendDeAggroPacket(@NotNull LivingDeathEvent event) {
        LivingEntity target = getCurrentTarget(event.getEntity());
        final boolean WAS_TARGETING_PLAYER = target instanceof ServerPlayer;

        return WAS_TARGETING_PLAYER;
    }

    private static boolean shouldSendDeAggroPacket(@NotNull LivingChangeTargetEvent event) {
        LivingEntity oldTarget = getCurrentTarget(event.getEntity());
        LivingEntity newTarget = event.getNewTarget();

        final boolean WAS_TARGETING_PLAYER = oldTarget instanceof ServerPlayer;

        final boolean NEW_TARGET_IS_DIFFERENT = newTarget == null || !newTarget.is(oldTarget);

        return WAS_TARGETING_PLAYER && NEW_TARGET_IS_DIFFERENT;
    }

    private static boolean shouldSendAggroPacket(@NotNull LivingChangeTargetEvent event) {
        LivingEntity oldTarget = getCurrentTarget(event.getEntity());
        LivingEntity newTarget = event.getNewTarget();

        String entityRegistryName = Objects.requireNonNull(
                BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType())).toString();
        boolean IS_BLACKLISTED = false;
        for (String item : ServerConfig.SERVER_MOB_BLACKLIST.get()
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

        final boolean IS_TARGETING_PLAYER = newTarget instanceof ServerPlayer;

        final boolean NEW_TARGET_IS_DIFFERENT = oldTarget == null || !oldTarget.is(newTarget);

        return IS_TARGETING_PLAYER && NEW_TARGET_IS_DIFFERENT;
    }

    private static LivingEntity getCurrentTarget(LivingEntity entity) {
        if (!(entity instanceof Mob)) {
            return null;
        }
        Mob mob = (Mob) entity;
        return mob.getTarget();
    }
}