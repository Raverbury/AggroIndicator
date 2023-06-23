package com.github.raverbury.aggroindicator.network.packet;

import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MobDeAggroPacket {

    public final UUID mobUuid;

    public MobDeAggroPacket(UUID _mobUuid) {
        mobUuid = _mobUuid;
    }

    public MobDeAggroPacket(FriendlyByteBuf buf) {
        mobUuid = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(mobUuid);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> AlertRenderer.setTarget(mobUuid, null)));
        context.setPacketHandled(true);
        return true;
    }
}
