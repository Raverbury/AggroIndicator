package io.github.raverbury.aggroindicator.client;

import io.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class ClientPayloadHandler {
    public static void handleData(final S2CMobChangeTargetPacket data,
                                  final IPayloadContext context) {
        // Do something with the data, on the main thread
        context.enqueueWork(() -> {
                    if (data.isPlayerNewTarget()) {
                        AlertRenderer.addAggroingMob(
                                UUID.fromString(data.mobUuidString()));
                    } else {
                        AlertRenderer.removeAggroingMob(
                                UUID.fromString(data.mobUuidString()));
                    }
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(
                            Component.translatable("my_mod.networking.failed",
                                    e.getMessage()));
                    return null;
                });
    }
}
