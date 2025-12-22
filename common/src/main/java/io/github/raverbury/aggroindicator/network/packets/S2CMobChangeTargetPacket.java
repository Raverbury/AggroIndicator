package io.github.raverbury.aggroindicator.network.packets;

import io.github.raverbury.aggroindicator.Constants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record S2CMobChangeTargetPacket(UUID mobUuid,
                                       boolean targetThisPlayer,
                                       boolean isAboutToAttack) implements CustomPacketPayload {
    public static final String ID = "mob_change_target";
    public static final CustomPacketPayload.Type<S2CMobChangeTargetPacket> PACKET_TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, ID));
    public static final StreamCodec<RegistryFriendlyByteBuf,
            S2CMobChangeTargetPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, S2CMobChangeTargetPacket::mobUuid,
                    ByteBufCodecs.BOOL,
                    S2CMobChangeTargetPacket::targetThisPlayer,
                    ByteBufCodecs.BOOL,
                    S2CMobChangeTargetPacket::isAboutToAttack,
                    S2CMobChangeTargetPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}