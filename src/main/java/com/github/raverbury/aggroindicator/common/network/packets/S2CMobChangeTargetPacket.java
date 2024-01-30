package com.github.raverbury.aggroindicator.common.network.packets;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record S2CMobChangeTargetPacket(UUID mobUuid, boolean playerIsNewTarget) implements FabricPacket {

    public static final PacketType<S2CMobChangeTargetPacket> PACKET_TYPE = PacketType.create(
            new Identifier(AggroIndicator.MOD_ID, "s2c_mob_change_target"),
            S2CMobChangeTargetPacket::new
    );

    public S2CMobChangeTargetPacket(PacketByteBuf buf) {
        this(buf.readUuid(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.mobUuid);
        buf.writeBoolean(this.playerIsNewTarget);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }
}
