package io.github.raverbury.aggroindicator.neoforge;


import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.client.AlertRenderer;
import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class AggroIndicatorNeoForge {

    public AggroIndicatorNeoForge(IEventBus eventBus) {
        eventBus.addListener(this::registerPacketHandler);
    }

    private void registerPacketHandler(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.optional().playToClient(
                S2CMobChangeTargetPacket.PACKET_TYPE,
                S2CMobChangeTargetPacket.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                                if (payload.targetThisPlayer()) {
                                    AlertRenderer.addAggroingMob(
                                            payload.mobUuid());
                                } else {
                                    AlertRenderer.removeAggroingMob(
                                            payload.mobUuid());
                                }
                            })
                            .exceptionally(e -> {
                                // Handle exception
                                context.disconnect(
                                        Component.translatable(
                                                "aggroindicator" +
                                                        ".networking.failed",
                                                e.getMessage()));
                                return null;
                            });
                }
        );
    }
}