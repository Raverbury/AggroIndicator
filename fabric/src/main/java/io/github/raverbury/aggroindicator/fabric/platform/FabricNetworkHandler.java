package io.github.raverbury.aggroindicator.fabric.platform;

import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.platform.services.INetworkHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class FabricNetworkHandler implements INetworkHandler {
    @Override
    public void SendS2CMobTargetPlayerPacket(ServerPlayer player, UUID mobUuid, boolean targetThisPlayer, boolean isAboutToAttack) {
        // TODO: send packet
        Constants.LOG.info("Fabric networking");
        // ServerPlayNetworking.send(player)
    }
}
