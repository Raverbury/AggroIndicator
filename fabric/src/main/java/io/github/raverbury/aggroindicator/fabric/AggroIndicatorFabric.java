package io.github.raverbury.aggroindicator.fabric;

import io.github.raverbury.aggroindicator.modules.AggroSoundPlayer;
import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class AggroIndicatorFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // fabric on 1.21.1 needs to init even earlier, common just doesn't
        // cut it
        // todo: init is called twice, while for now empty, could be
        //  problematic in the future if code is ever added
        AggroSoundPlayer.init();
        PayloadTypeRegistry.playS2C().register(S2CMobChangeTargetPacket.PACKET_TYPE, S2CMobChangeTargetPacket.CODEC);
    }
}
