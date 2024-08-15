package io.github.raverbury.aggroindicator.common;

import io.github.raverbury.aggroindicator.common.events.CustomLivingChangeTargetEvent;
import io.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import io.github.raverbury.aggroindicator.common.mixins.LivingEntityAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommonEventHandler {

    private static final Map<UUID, UUID> aggroList = new HashMap<>();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                CommonEventHandler::assignOwnerToBrainOnEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(
                CommonEventHandler::clearAggroForLoggedOutPlayer);
        NeoForge.EVENT_BUS.addListener(
                CommonEventHandler::handleLivingChangeTargetEvent);
        NeoForge.EVENT_BUS.addListener(
                CommonEventHandler::handleCustomLivingChangeTargetEvent);
        NeoForge.EVENT_BUS.addListener(
                CommonEventHandler::handleLivingDeathEvent);
        NeoForge.EVENT_BUS.addListener(
                CommonEventHandler::handleWorldUnloadEvent);
    }

    public static void handleLivingChangeTargetEvent(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob) && event.getEntity().level()
                .isClientSide()) {
            return;
        }
        processAggroChange((Mob) event.getEntity(), event.getNewTarget());
    }

    public static void handleCustomLivingChangeTargetEvent(CustomLivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob) && event.getEntity().level()
                .isClientSide()) {
            return;
        }
        processAggroChange((Mob) event.getEntity(), event.getTarget());
    }

    /**
     * Assign custom brainOwner field in BrainMixin with owner
     *
     * @param event
     */
    public static void assignOwnerToBrainOnEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            ((BrainAccess) livingEntity.getBrain()).aggroindicator$setBrainOwner(
                    livingEntity);
        }
    }

    /**
     * Clear aggro list of mobs targeting a player that just disconnects
     *
     * @param event
     */
    public static void clearAggroForLoggedOutPlayer(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        for (UUID mobUuid : aggroList.keySet()) {
            if (aggroList.get(mobUuid) == event.getEntity().getUUID()) {
                aggroList.remove(mobUuid);
            }
        }
    }

    /**
     * Clear the aggro list on server world unload
     *
     * @param event
     */
    public static void handleWorldUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        aggroList.clear();
    }

    public static void handleLivingDeathEvent(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity() == null || event.getEntity()
                .level()
                .isClientSide() || !(event.getEntity() instanceof Mob)) {
            return;
        }
        processAggroChange((Mob) event.getEntity(), null);
    }

    private static void processAggroChange(@Nonnull Mob mob, @Nullable LivingEntity newTarget) {
        if (((LivingEntityAccess) mob).getDead()) {
            return;
        }

        @Nullable LivingEntity oldTarget = getOldTarget(mob);

        // if target hasn't changed then skip
        if (oldTarget == newTarget) {
            return;
        }

        // AggroIndicator.LOGGER.info(mob.getName()
        //         .getString() + " switches target from " + (oldTarget != null ? oldTarget.getName()
        //         .getString() : "null") + " to " + (newTarget != null ? newTarget.getName()
        //         .getString() : "null"));

        // send deaggro packet for old target
        if (shouldSendDeAggroPacket(oldTarget)) {
            PacketDistributor.sendToPlayer((ServerPlayer) oldTarget,
                    new S2CMobChangeTargetPacket(mob.getUUID().toString(),
                            false));
        }

        // send aggro packet for new target
        if (shouldSendAggroPacket(mob, newTarget)) {
            PacketDistributor.sendToPlayer((ServerPlayer) newTarget,
                    new S2CMobChangeTargetPacket(mob.getUUID().toString(),
                            true));
        }

        saveCurrentTarget(mob, newTarget);
    }

    private static boolean shouldSendDeAggroPacket(@Nullable LivingEntity oldTarget) {
        return oldTarget instanceof ServerPlayer;
    }

    private static boolean shouldSendAggroPacket(@Nonnull Mob mob, @Nullable LivingEntity newTarget) {
        String entityRegistryName = BuiltInRegistries.ENTITY_TYPE.getKey(
                mob.getType()).toString();
        boolean IS_BLACKLISTED = false;
        for (String item : CommonConfig.serverMobBlacklist) {
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

        return newTarget instanceof ServerPlayer;
    }

    /**
     * Get old target of mob, stored in aggroList
     *
     * @param livingEntity The mob to retrieve target from
     * @return The LivingEntity that is the target of the supplied mob
     */
    private static LivingEntity getOldTarget(LivingEntity livingEntity) {
        if (livingEntity instanceof Mob mob) {
            UUID oldTargetUUID = aggroList.computeIfAbsent(mob.getUUID(),
                    uuid -> {
                        return null;
                    });
            if (oldTargetUUID == null) {
                return null;
            }
            if (mob.level() instanceof ServerLevel serverLevel) {
                return (LivingEntity) serverLevel.getEntity(oldTargetUUID);
            }
            return null;
        }
        return null;
    }

    /**
     * Write current target in aggro list
     *
     * @param mob           The mob to save target to
     * @param currentTarget The mob to retrieve target from
     */
    private static void saveCurrentTarget(Mob mob, @Nullable LivingEntity currentTarget) {
        UUID targetUUID = null;
        if (currentTarget != null) {
            targetUUID = currentTarget.getUUID();
        }
        if (targetUUID == null) {
            aggroList.remove(mob.getUUID());
        } else {
            aggroList.put(mob.getUUID(), targetUUID);
        }
    }
}
