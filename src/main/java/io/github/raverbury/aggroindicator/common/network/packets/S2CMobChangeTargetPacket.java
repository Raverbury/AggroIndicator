package io.github.raverbury.aggroindicator.common.network.packets;

import io.github.raverbury.aggroindicator.AggroIndicator;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record S2CMobChangeTargetPacket(String mobUuidString,
                                       boolean isPlayerNewTarget) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CMobChangeTargetPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AggroIndicator.MODID,
                    "s2c_mob_change_target"));

    public static final StreamCodec<ByteBuf, S2CMobChangeTargetPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, S2CMobChangeTargetPacket::mobUuidString,
            ByteBufCodecs.BOOL, S2CMobChangeTargetPacket::isPlayerNewTarget,
            S2CMobChangeTargetPacket::new);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

