package com.github.raverbury.aggroindicator.network.packet;

import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MobTargetPlayerPacket {

    public final UUID mobUuid;
    public final UUID playerUuid;
    public final boolean isAboutToAttack;

    public MobTargetPlayerPacket(UUID _mobUuid, UUID _playerUuid, boolean _isAboutToAttack) {
        mobUuid = _mobUuid;
        playerUuid = _playerUuid;
        isAboutToAttack = _isAboutToAttack;
    }

    public MobTargetPlayerPacket(FriendlyByteBuf buf) {
        mobUuid = buf.readUUID();
        playerUuid = buf.readUUID();
        isAboutToAttack = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(mobUuid);
        buf.writeUUID(playerUuid);
        buf.writeBoolean(isAboutToAttack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> AlertRenderer.setTarget(mobUuid, playerUuid, isAboutToAttack)));
        context.setPacketHandled(true);
        return true;
    }
}
