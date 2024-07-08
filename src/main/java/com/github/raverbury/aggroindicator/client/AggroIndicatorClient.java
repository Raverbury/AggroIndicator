package com.github.raverbury.aggroindicator.client;

import com.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;

import java.util.List;

public class AggroIndicatorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerClientEventHandlers();
        registerClientPacketHandlers();
    }

    private void registerClientEventHandlers() {
        // clear list of aggroing entities on client disconnect from any world
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            AlertRenderer.clearMobsTargetingThisClientPlayer();
        });
        // check rendered entities for eligibility to show aggro icon
        WorldRenderEvents.AFTER_ENTITIES.register((worldRenderContext) -> {
            if (!worldRenderContext.world().isClient()) {
                return;
            }

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            ClientWorld world = MinecraftClient.getInstance().world;
            if (player == null || world == null) {
                return;
            }

            // if (player.hasStatusEffect(StatusEffects.BLINDNESS)
            //         || player.hasStatusEffect(StatusEffects.DARKNESS)) {
            //     return;
            // }

            List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class,
                    player.getBoundingBox().expand(32),
                    (mob) -> true);

            for (MobEntity mob : mobs) {
                if (mob.hasStatusEffect(
                        StatusEffects.INVISIBILITY) || mob.isInvisible()) {
                    continue;
                }
                if (!AlertRenderer.shouldDrawThisUuid(mob.getUuid())) {
                    continue;
                }
                AlertRenderer.addRenderedEntities(mob);
            }

            AlertRenderer.renderAlertIcon(worldRenderContext.tickDelta(),
                    worldRenderContext.matrixStack(),
                    worldRenderContext.camera());
        });
    }

    private void registerClientPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(
                S2CMobChangeTargetPacket.PACKET_TYPE,
                (packet, player, responseSender) -> {
                    if (packet.playerIsNewTarget()) {
                        AlertRenderer.addMobTargetingThisClientPlayer(
                                packet.mobUuid());
                    } else {
                        AlertRenderer.removeMobTargetingThisClientPlayer(
                                packet.mobUuid());
                    }
                });
    }
}
