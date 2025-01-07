package io.github.raverbury.aggroindicator.fabric.platform;

import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import io.github.raverbury.aggroindicator.platform.services.INetworkHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class FabricNetworkHandler implements INetworkHandler {
    @Override
    public void sendS2CMobTargetPlayerPacket(ServerPlayer player, UUID mobUuid, boolean targetThisPlayer, boolean isAboutToAttack) {
        ServerPlayNetworking.send(player,
                new S2CMobChangeTargetPacket(mobUuid, targetThisPlayer,
                        isAboutToAttack));
    }
}
