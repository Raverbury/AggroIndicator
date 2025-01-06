package io.github.raverbury.aggroindicator.fabric;

import io.github.raverbury.aggroindicator.CommonClass;
import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public class AggroIndicatorFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(S2CMobChangeTargetPacket.PACKET_TYPE, S2CMobChangeTargetPacket.CODEC);
    }
}
