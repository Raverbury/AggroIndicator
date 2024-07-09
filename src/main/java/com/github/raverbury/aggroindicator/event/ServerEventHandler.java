package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.mixin.LivingEntityAccess;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.github.raverbury.aggroindicator.network.packet.S2CMobChangeTargetPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;

public class ServerEventHandler {

    private static final Map<UUID, UUID> aggroList = new HashMap<>();

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(
                ServerEventHandler::handleEntityPostTickEvent);
        MinecraftForge.EVENT_BUS.addListener(
                ServerEventHandler::handleLivingDeathEvent);
        MinecraftForge.EVENT_BUS.addListener(
                ServerEventHandler::handleWorldUnloadEvent);
    }

    public static void handleWorldUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        aggroList.clear();
    }

    public static void handleEntityPostTickEvent(EntityPostTickEvent event) {
        if (event.getEntity() == null || event.getEntity().level()
                .isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        // edge case with goat, handled by ram related mixins
        // so we exclude that here, ugly, but it works
        if (entity instanceof Mob mob && !(entity instanceof Goat)) {
            LivingEntity oldTarget = getOldTarget(mob);

            // get current target the normal way
            LivingEntity newTarget = mob.getTarget();
            // if null then try reading from memory
            if (newTarget == null) {
                Optional<LivingEntity> optionalTarget = mob.getBrain()
                        .getMemoryInternal(MemoryModuleType.ATTACK_TARGET);
                if (optionalTarget != null && optionalTarget.isPresent()) {
                    newTarget = optionalTarget.get();
                }
            }

            // if target has changed, dispatch lct
            if (newTarget != oldTarget) {
                processAggroChange(mob, newTarget);
            }
        }
    }

    public static void handleLivingDeathEvent(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity() == null || event.getEntity()
                .level()
                .isClientSide() || !(event.getEntity() instanceof Mob)) {
            return;
        }
        processAggroChange((Mob) event.getEntity(), null);
    }

    public static void processAggroChange(@Nonnull Mob mob, @Nullable LivingEntity newTarget) {
        if (mob.level().isClientSide()) {
            return;
        }
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
            NetworkHandler.sendToPlayer(
                    new S2CMobChangeTargetPacket(mob.getUUID(), false),
                    (ServerPlayer) oldTarget);
        }

        // send aggro packet for new target
        if (shouldSendAggroPacket(mob, newTarget)) {
            NetworkHandler.sendToPlayer(
                    new S2CMobChangeTargetPacket(mob.getUUID(), true),
                    (ServerPlayer) newTarget);
        }

        saveCurrentTarget(mob, newTarget);
    }

    private static boolean shouldSendDeAggroPacket(@Nullable LivingEntity oldTarget) {
        return oldTarget instanceof ServerPlayer;
    }

    private static boolean shouldSendAggroPacket(@Nonnull Mob mob, @Nullable LivingEntity newTarget) {
        String entityRegistryName = Objects.requireNonNull(
                ForgeRegistries.ENTITY_TYPES.getKey(mob.getType())).toString();
        boolean IS_BLACKLISTED = false;
        for (String item : ServerConfig.SERVER_MOB_BLACKLIST.get()) {
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
     * @param mob           The mob to retrieve target from
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
