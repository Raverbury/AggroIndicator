package io.github.raverbury.aggroindicator.platform.services;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface INetworkHandler {
    public void sendS2CMobTargetPlayerPacket(ServerPlayer player,
                                             UUID mobUuid,
                                             boolean targetThisPlayer,
                                             boolean isAboutToAttack);
}
