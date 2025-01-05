package io.github.raverbury.aggroindicator;

import io.github.raverbury.aggroindicator.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {

    private static final Map<UUID, UUID> mobTargetPlayerMap = new HashMap<>();

    public static void init() {
    }

    public static void livingChangeTarget(LivingEntity attacker,
                                          LivingEntity newTarget) {
        if (!(attacker instanceof Mob) || attacker.level().isClientSide) {
            return;
        }

        LivingEntity oldTarget = getOldTarget((Mob) attacker);

        if (oldTarget == newTarget) {
            return;
        }

        // send deaggro to old target
        if (shouldSendDeaggroPacket(oldTarget)) {
            Services.NETWORK.SendS2CMobTargetPlayerPacket(
                    (ServerPlayer) oldTarget,
                    attacker.getUUID(), false, false);
        }

        // send aggro to new target
        if (shouldSendAggroPacket((Mob) attacker, newTarget)) {
            Services.NETWORK.SendS2CMobTargetPlayerPacket(
                    (ServerPlayer) newTarget,
                    attacker.getUUID(), true, false);
        }

        // save current target for this mob
        saveCurrentTarget((Mob) attacker, newTarget);
    }

    private static boolean shouldSendDeaggroPacket(LivingEntity oldTarget) {
        return oldTarget instanceof ServerPlayer;
    }

    private static boolean shouldSendAggroPacket(Mob mob,
                                                 LivingEntity newTarget) {
        if (!(newTarget instanceof ServerPlayer)) {
            return false;
        }

        String entityRegName =
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();

        // TODO: read blacklist from config
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
    private static void saveCurrentTarget(Mob attacker,
                                          LivingEntity currentTarget) {
        UUID targetUUID = null;
        if (currentTarget != null) {
            targetUUID = currentTarget.getUUID();
        }
        if (targetUUID == null) {
            mobTargetPlayerMap.remove(attacker.getUUID());
        } else {
            mobTargetPlayerMap.put(attacker.getUUID(), targetUUID);
        }
    }

    /**
     * Get old target of mob, stored in aggroList
     *
     * @param attacker The mob to retrieve target from
     * @return The LivingEntity that is the target of the supplied mob
     */
    private static LivingEntity getOldTarget(Mob attacker) {
        UUID oldTargetUUID = mobTargetPlayerMap.computeIfAbsent(
                attacker.getUUID(),
                uuid -> {
                    return null;
                });
        if (oldTargetUUID == null) {
            return null;
        }
        if (attacker.level() instanceof ServerLevel serverLevel) {
            return (LivingEntity) serverLevel.getEntity(oldTargetUUID);
        }
        return null;
    }
}
