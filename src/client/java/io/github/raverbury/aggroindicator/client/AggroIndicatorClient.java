package io.github.raverbury.aggroindicator.client;

import io.github.raverbury.aggroindicator.client.config.ClientConfig;
import io.github.raverbury.aggroindicator.network.packet.S2CMobChangeTargetPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class AggroIndicatorClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientConfig.save(ClientConfig.loadOrDefault());
		registerClientEventHandlers();
		registerClientPacketHandlers();
	}

	private void registerClientEventHandlers() {
		WorldRenderEvents.AFTER_ENTITIES.register((worldRenderContext) -> {
			if (!worldRenderContext.world().isClient()) {
				return;
			}

			AlertRenderer.renderAlertIcon(worldRenderContext.tickDelta(),
					worldRenderContext.matrixStack(), worldRenderContext.camera());
		});
	}

	private void registerClientPacketHandlers() {
		ClientPlayNetworking.registerGlobalReceiver(
				S2CMobChangeTargetPacket.PACKET_TYPE,
				(packet, player, responseSender) -> {
					if (packet.playerIsNewTarget()) {
						AlertRenderer.addAggroingMob(
								packet.mobUuid(), false);
					} else {
						AlertRenderer.removeAggroingMob(
								packet.mobUuid());
					}
				});
	}
}