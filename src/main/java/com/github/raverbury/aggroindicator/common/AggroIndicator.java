package com.github.raverbury.aggroindicator.common;

import com.github.raverbury.aggroindicator.common.events.EntityTickEventCallback;
import com.github.raverbury.aggroindicator.common.events.LivingChangeTargetCallback;
import com.github.raverbury.aggroindicator.common.mixins.LivingEntityAccess;
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

    /**
     * A non-persistent attachment type used to store targeting information
     */
    public static final AttachmentType<UUID> TARGET_UUID_ATTACHMENT = AttachmentRegistry.create(
            new Identifier(MOD_ID, "attack_target_uuid"));

    /**
     * Save current target as attachment to mob using Fabric API's attachment
     *
     * @param entity
     * @param target
     */
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

    /**
     * Read old target from attachment or resolve to null if empty
     *
     * @param entity
     */
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
        // handler responsible for sending targeting packets to players
        // and saving targeting info to mobs
        LivingChangeTargetCallback.EVENT.register((mob, newTarget) -> {
            if (mob.getWorld().isClient) {
                return ActionResult.PASS;
            }
            if (((LivingEntityAccess) mob).getDead()) {
                return ActionResult.PASS;
            }

            @Nullable LivingEntity oldTarget = getOldTarget(mob);

            // if target hasn't changed then skip
            if (oldTarget == newTarget) {
                return ActionResult.PASS;
            }

            // LOGGER.info(mob.getName()
            //         .getString() + " switches target from " + (oldTarget != null ? oldTarget.getName()
            //         .getString() : "null") + " to " + (newTarget != null ? newTarget.getName()
            //         .getString() : "null"));

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
            // this is needed over Mob#getTarget since some newer mobs
            // don't touch the Mob#target property at all
            // also stores info for future comparison
            saveCurrentTarget(mob, newTarget);

            return ActionResult.PASS;
        });
        // this should fire after an entity is ticked
        // so its current target will be the new target
        // it has acquired during that tick
        // I have to resort to do it this way since I'm sick of having to
        // look in 10 different tasks to know what sets and unsets memories
        // 7 extra mixins just to effectively check ATTACK_TARGET
        // memory is not it
        EntityTickEventCallback.EVENT.register(entity -> {
            if (entity.getWorld().isClient()) {
                return ActionResult.PASS;
            }

            // edge case with goat, handled by ram related mixins
            // so we exclude that here, ugly, but it works
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