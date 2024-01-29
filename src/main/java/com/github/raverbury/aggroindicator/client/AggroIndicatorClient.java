package com.github.raverbury.aggroindicator.client;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.mixin.client.rendering.WorldRendererMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.mob.MobEntity;

import java.util.List;

public class AggroIndicatorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register((worldRenderContext) -> {
            if (!worldRenderContext.world().isClient()) {
                return;
            }

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            ClientWorld world = MinecraftClient.getInstance().world;
            if (player == null || world == null) {
                return;
            }

            List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(32), (mob) -> {
                return true;
            });

            // AggroIndicator.LOGGER.info(entities.toString());

            // TODO: add checks and register mob to alert renderer

            for (MobEntity mob: mobs) {
                AlertRenderer.addEntity(mob);
            }

            AlertRenderer.renderAlertIcon(worldRenderContext.tickDelta(), worldRenderContext.matrixStack(), worldRenderContext.camera());

        });
    }
}
