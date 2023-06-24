package com.github.raverbury.aggroindicator.event;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.github.raverbury.aggroindicator.network.packet.MobDeAggroPacket;
import com.github.raverbury.aggroindicator.network.packet.MobTargetPlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ServerEventHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingChangeTargetEvent);
        MinecraftForge.EVENT_BUS.addListener(ServerEventHandler::handleLivingDeathEvent);
    }

    public static void handleLivingChangeTargetEvent(LivingChangeTargetEvent event) {
        // AggroIndicator.LOGGER.debug("LCTE fired" + ((getCurrentTarget(event.getEntity()) != null)? getCurrentTarget(event.getEntity()).toString() : "no og target") + ((event.getNewTarget() != null)? event.getNewTarget().toString() : "no new target"));
        if (event.isCanceled() || event.getEntity() == null || event.getEntity().level.isClientSide()) {
            return;
        }
        if (shouldSendDeAggroPacket(event)) {
            NetworkHandler.sendToPlayer(new MobDeAggroPacket(event.getEntity().getUUID()), (ServerPlayer) getCurrentTarget(event.getEntity()));
            AggroIndicator.LOGGER.debug("Should send deaggro packet");
        }
        if (shouldSendAggroPacket(event)) {
            NetworkHandler.sendToPlayer(new MobTargetPlayerPacket(event.getEntity().getUUID(), event.getNewTarget().getUUID()), (ServerPlayer) event.getNewTarget());
            AggroIndicator.LOGGER.debug("Should send aggro packet");
        }
    }

    public static void handleLivingDeathEvent(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity() == null || event.getEntity().level.isClientSide()) {
            return;
        }
        if (shouldSendDeAggroPacket(event)) {
            AggroIndicator.LOGGER.debug("Should send death deaggro packet");
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
