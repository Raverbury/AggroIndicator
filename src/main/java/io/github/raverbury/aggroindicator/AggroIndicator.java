package io.github.raverbury.aggroindicator;

import io.github.raverbury.aggroindicator.network.packet.S2CMobChangeTargetPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AggroIndicator implements ModInitializer {
    public static final String MOD_ID = "aggroindicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<UUID, UUID> mobTargetPlayerMap = new HashMap<>();

    public static void livingChangeTarget(LivingEntity attacker,
                                          LivingEntity newTarget) {
        if (!(attacker instanceof MobEntity) || attacker.getWorld()
                .isClient()) {
            return;
        }

        LivingEntity oldTarget = getOldTarget((MobEntity) attacker);

        if (oldTarget == newTarget) {
            return;
        }

        // send deaggro to old target
        if (shouldSendDeaggroPacket(oldTarget)) {
            S2CMobChangeTargetPacket packet =
                    new S2CMobChangeTargetPacket(attacker.getUuid(), false);
            ServerPlayNetworking.send((ServerPlayerEntity) oldTarget, packet);
        }

        // send aggro to new target
        if (shouldSendAggroPacket((MobEntity) attacker, newTarget)) {
            S2CMobChangeTargetPacket packet =
                    new S2CMobChangeTargetPacket(attacker.getUuid(), true);
            ServerPlayNetworking.send((ServerPlayerEntity) newTarget, packet);
        }

        // save current target for this mob
        saveCurrentTarget((MobEntity) attacker, newTarget);
    }

    public static void clearMobTargetPlayerMap() {
        mobTargetPlayerMap.clear();
    }

    public static void clearMobTargetingThisPlayer(ServerPlayerEntity player) {
        while (mobTargetPlayerMap.values().remove(player.getUuid())) ;
    }

    private static boolean shouldSendDeaggroPacket(LivingEntity oldTarget) {
        return oldTarget instanceof ServerPlayerEntity;
    }

    private static boolean shouldSendAggroPacket(MobEntity mob,
                                                 LivingEntity newTarget) {
        if (!(newTarget instanceof ServerPlayerEntity)) {
            return false;
        }

        // TODO: read blacklist from server config?
        // String entityRegName =
        //         BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        // for (String blacklistedMobId : ) {
        //     blacklistedMobId.replace("*", ".*");
        //     Pattern pattern = Pattern.compile(blacklistedMobId,
        //             Pattern.CASE_INSENSITIVE);
        //     if (pattern.matcher(entityRegName).matches()) {
        //         return false;
        //     }
        // }

        return true;
    }

    /**
     * Write current target of mob to aggro map
     *
     * @param attacker      The mob to save target to
     * @param currentTarget The mob to retrieve target from
     */
    private static void saveCurrentTarget(MobEntity attacker,
                                          LivingEntity currentTarget) {
        UUID targetUUID = null;
        if (currentTarget instanceof PlayerEntity) {
            targetUUID = currentTarget.getUuid();
        }
        if (targetUUID == null) {
            mobTargetPlayerMap.remove(attacker.getUuid());
        } else {
            mobTargetPlayerMap.put(attacker.getUuid(), targetUUID);
        }
    }

    /**
     * Get old target of mob, stored in aggroList
     *
     * @param attacker The mob to retrieve target from
     * @return The LivingEntity that is the target of the supplied mob
     */
    private static LivingEntity getOldTarget(MobEntity attacker) {
        UUID oldTargetUUID = mobTargetPlayerMap.computeIfAbsent(
                attacker.getUuid(),
                uuid -> {
                    return null;
                });
        if (oldTargetUUID == null) {
            return null;
        }
        if (attacker.getWorld() instanceof ServerWorld serverLevel) {
            return (LivingEntity) serverLevel.getEntity(oldTargetUUID);
        }
        return null;
    }

    @Override
    public void onInitialize() {
    }
}