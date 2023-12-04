package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.util.IAggroIndicatorAttackGoal;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.github.raverbury.aggroindicator.network.packet.MobDeAggroPacket;
import com.github.raverbury.aggroindicator.network.packet.MobTargetPlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ServerEventHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingChangeTargetEvent);
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingDeathEvent);
    }

    public static void handleLivingChangeTargetEvent(LivingChangeTargetEvent event) {
        // AggroIndicator.LOGGER.debug("LCTE fired" + ((getCurrentTarget(event.getEntity()) != null)? getCurrentTarget(event.getEntity()).toString() : "no og target") + ((event.getNewTarget() != null)? event.getNewTarget().toString() : "no new target"));
        if (event.isCanceled() || event.getEntity() == null || event.getEntity().level().isClientSide()) {
            return;
        }
        if (shouldSendDeAggroPacket(event)) {
            NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().getUUID()), (ServerPlayer) getCurrentTarget(event.getEntity()));
//            AggroIndicator.LOGGER.debug("Should send deaggro packet");
        }
        if (shouldSendAggroPacket(event)) {
//            AggroIndicator.LOGGER.debug("Should send aggro packet");
            NetworkHandler.sendToPlayer(new MobTargetPlayerPacket(event.getEntity().getUUID(), event.getNewTarget().getUUID(), isAboutToAttack(event.getEntity())), (ServerPlayer) event.getNewTarget());
        }
    }

    public static boolean isAboutToAttack(LivingEntity attacker) {
        Mob mob = (Mob) attacker;
        Set<WrappedGoal> goals = mob.goalSelector.getAvailableGoals();
        final boolean[] a = {false};
        goals.forEach(wrappedGoal -> {
//            AggroIndicator.LOGGER.info(wrappedGoal.getGoal().toString());
            IAggroIndicatorAttackGoal aggroIndicatorAttackGoal = (wrappedGoal.getGoal() instanceof IAggroIndicatorAttackGoal)?
                    (IAggroIndicatorAttackGoal) wrappedGoal.getGoal() : null;
            if (aggroIndicatorAttackGoal != null) {
//                AggroIndicator.LOGGER.info(aggroIndicatorAttackGoal.isAboutToAttack(getCurrentTarget(mob))? "YO" : "NO");
                if (aggroIndicatorAttackGoal.isAboutToAttack(getCurrentTarget(mob))) {
                    a[0] = true;
                }
            }
        });
//        AggroIndicator.LOGGER.info(a[0]? "KILL" : "NO KILL pls");
        return a[0];
    }

    public static void handleLivingDeathEvent(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity() == null || event.getEntity().level().isClientSide()) {
            return;
        }
        if (shouldSendDeAggroPacket(event)) {
//            AggroIndicator.LOGGER.debug("Should send death deaggro packet");
            ServerPlayer serverPlayer = (ServerPlayer) ((Mob) event.getEntity()).getTarget();
            NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().getUUID()), serverPlayer);
        }
    }

    private static boolean shouldSendDeAggroPacket(LivingDeathEvent event) {
        LivingEntity target = getCurrentTarget(event.getEntity());
        final boolean WAS_TARGETING_PLAYER = target instanceof ServerPlayer;

        return WAS_TARGETING_PLAYER;
    }

    private static boolean shouldSendDeAggroPacket(LivingChangeTargetEvent event) {
        LivingEntity oldTarget = getCurrentTarget(event.getEntity());
        LivingEntity newTarget = event.getNewTarget();

        final boolean WAS_TARGETING_PLAYER = oldTarget instanceof ServerPlayer;

        final boolean NEW_TARGET_IS_DIFFERENT = newTarget == null || !newTarget.is(oldTarget);

        return WAS_TARGETING_PLAYER && NEW_TARGET_IS_DIFFERENT;
    }

    private static boolean shouldSendAggroPacket(LivingChangeTargetEvent event) {
        LivingEntity oldTarget = getCurrentTarget(event.getEntity());
        LivingEntity newTarget = event.getNewTarget();

        String entityRegistryName = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType())).toString();
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
            return false;
        }

        final boolean IS_TARGETING_PLAYER = newTarget instanceof ServerPlayer;

        final boolean NEW_TARGET_IS_DIFFERENT = oldTarget == null || !oldTarget.is(newTarget);

        return IS_TARGETING_PLAYER;
    }

    private static LivingEntity getCurrentTarget(LivingEntity entity) {
        if (!(entity instanceof Mob)) {
            return null;
        }
        Mob mob = (Mob) entity;
        return mob.getTarget();
    }
}
