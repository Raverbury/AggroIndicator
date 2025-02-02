package io.github.raverbury.aggroindicator.fabric.client;

import io.github.raverbury.aggroindicator.client.AlertRenderer;
import io.github.raverbury.aggroindicator.client.CommonClientClass;
import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AggroIndicatorFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommonClientClass.init();
        ClientPlayNetworking.registerGlobalReceiver(
                S2CMobChangeTargetPacket.PACKET_TYPE,
                ((payload, context) -> {
                    context.client().execute(() -> {
                        if (payload.targetThisPlayer()) {
                            AlertRenderer.addAggroingMob(
                                    payload.mobUuid(), payload.isAboutToAttack());
                        } else {
                            AlertRenderer.removeAggroingMob(
                                    payload.mobUuid());
                        }
                    });
                }));
    }
}
