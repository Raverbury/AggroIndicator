package com.github.raverbury.aggroindicator.network.packet;

import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CMobChangeTargetPacket {
    public final UUID mobUuid;
    public final boolean playerIsNewTarget;

    public S2CMobChangeTargetPacket(UUID _mobUuid, boolean _playerIsNewTarget) {
        this.mobUuid = _mobUuid;
        this.playerIsNewTarget = _playerIsNewTarget;
    }

    public S2CMobChangeTargetPacket(FriendlyByteBuf buf) {
        this.mobUuid = buf.readUUID();
        this.playerIsNewTarget = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {

        buf.writeUUID(this.mobUuid);
        buf.writeBoolean(this.playerIsNewTarget);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(
                () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    if (this.playerIsNewTarget) {
                        AlertRenderer.addAggroingMob(this.mobUuid);
                    } else {
                        AlertRenderer.removeAggroingMob(this.mobUuid);
                    }
                }));
        context.setPacketHandled(true);
        return true;
    }
}
