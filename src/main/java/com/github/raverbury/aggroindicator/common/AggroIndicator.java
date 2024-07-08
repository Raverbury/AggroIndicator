package com.github.raverbury.aggroindicator.common;

import com.github.raverbury.aggroindicator.common.events.EntityTickEventCallback;
import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import com.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class AggroIndicator implements ModInitializer {

    public static final String MOD_ID = "aggroindicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final AttachmentType<UUID> TARGET_UUID_ATTACHMENT = AttachmentRegistry.create(
            new Identifier(MOD_ID, "attack_target_uuid"));

    private static void saveCurrentTarget(LivingEntity entity, LivingEntity target) {
        if (!(entity instanceof MobEntity)) {
            return;
        }
        if (target == null) {
            entity.removeAttached(TARGET_UUID_ATTACHMENT);
        } else {
            entity.setAttached(TARGET_UUID_ATTACHMENT, target.getUuid());
        }
    }

    private static LivingEntity getOldTarget(LivingEntity entity) {
        UUID targetUuid = entity.getAttachedOrGet(TARGET_UUID_ATTACHMENT,
                () -> {
                    return null;
                });
        if (targetUuid != null && entity.getWorld() instanceof ServerWorld serverWorld) {
            return (LivingEntity) serverWorld.getEntity(targetUuid);
        }
        return null;
    }

    @Override
    public void onInitialize() {
        registerCommonEventHandlers();
    }

    public void registerCommonEventHandlers() {
        LivingChangeTargetCallback.EVENT.register((mob, newTarget) -> {
            if (mob.getWorld().isClient) {
                return ActionResult.PASS;
            }

            @Nullable LivingEntity oldTarget = getOldTarget(mob);

            LOGGER.info(mob.getName()
                    .getString() + " switches target from " + (oldTarget != null ? oldTarget.getName()
                    .getString() : "null") + " to " + (newTarget != null ? newTarget.getName()
                    .getString() : "null"));

            // old target is player then send packet to that player with playerIsNewTarget = false
            if (oldTarget instanceof PlayerEntity) {
                S2CMobChangeTargetPacket packet1 = new S2CMobChangeTargetPacket(
                        mob.getUuid(), false);
                ServerPlayNetworking.send((ServerPlayerEntity) oldTarget,
                        packet1);
            }

            // new target is player then send packet to that player with playerIsNewTarget = true
            if (newTarget instanceof PlayerEntity) {
                S2CMobChangeTargetPacket packet1 = new S2CMobChangeTargetPacket(
                        mob.getUuid(), true);
                ServerPlayNetworking.send((ServerPlayerEntity) newTarget,
                        packet1);
            }

            // save this target as attachment to the mob
            // this is needed over Mob#getTarget since hog/zoglins
            // don't touch the Mob#target property at all
            // also stores info for the future
            saveCurrentTarget(mob, newTarget);

            return ActionResult.PASS;
        });
        EntityTickEventCallback.EVENT.register(entity -> {
            if (entity.getWorld().isClient()) {
                return ActionResult.PASS;
            }
            if (entity instanceof MobEntity mob && !(entity instanceof GoatEntity)) {
                LivingEntity oldTarget = getOldTarget(mob);
                // get current target the normal way
                LivingEntity newTarget = mob.getTarget();
                // if null then try reading from memory
                if (newTarget == null) {
                    Optional<LivingEntity> optionalTarget = mob.getBrain()
                            .getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
                    if (optionalTarget != null && optionalTarget.isPresent()) {
                        newTarget = optionalTarget.get();
                    }
                }
                // if target has changed, dispatch lct
                if (newTarget != oldTarget) {
                    LivingChangeTargetCallback.EVENT.invoker()
                            .interact(mob, newTarget);
                }
            }
            return ActionResult.PASS;
        });
    }
}