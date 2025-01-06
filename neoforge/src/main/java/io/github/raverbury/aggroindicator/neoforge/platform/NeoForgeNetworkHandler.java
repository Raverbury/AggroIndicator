package io.github.raverbury.aggroindicator.neoforge.platform;

import io.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import io.github.raverbury.aggroindicator.platform.services.INetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class NeoForgeNetworkHandler implements INetworkHandler {
    @Override
    public void sendS2CMobTargetPlayerPacket(ServerPlayer player, UUID mobUuid, boolean targetThisPlayer, boolean isAboutToAttack) {
        PacketDistributor.sendToPlayer(player,
                new S2CMobChangeTargetPacket(mobUuid, targetThisPlayer,
                        isAboutToAttack));
    }
}
