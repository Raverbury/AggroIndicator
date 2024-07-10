package io.github.raverbury.aggroindicator.common;

import io.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import io.github.raverbury.aggroindicator.mixins.LivingEntityAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.goat.Goat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommonEventHandler {

    private static final Map<UUID, UUID> aggroList = new HashMap<>();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(CommonEventHandler::handleEntityPostTickEvent);
        NeoForge.EVENT_BUS.addListener(CommonEventHandler::handleLivingDeathEvent);
        NeoForge.EVENT_BUS.addListener(CommonEventHandler::handleWorldUnloadEvent);
    }

    public static void handleWorldUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        aggroList.clear();
    }

    public static void handleEntityPostTickEvent(EntityTickEvent.Post event) {
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
