package io.github.raverbury.aggroindicator.neoforge.platform;

import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.platform.services.INetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class NeoForgeNetworkHandler implements INetworkHandler {
    @Override
    public void SendS2CMobTargetPlayerPacket(ServerPlayer player, UUID mobUuid, boolean targetThisPlayer, boolean isAboutToAttack) {
        Constants.LOG.info("NeoForge networking");
        // TODO: send packet
        // PacketDistributor.sendToPlayer(player);
    }
}
