package io.github.raverbury.aggroindicator.fabric;

import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class AggroIndicatorFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(S2CMobChangeTargetPacket.PACKET_TYPE, S2CMobChangeTargetPacket.CODEC);
    }
}
