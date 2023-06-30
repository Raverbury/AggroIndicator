package com.github.raverbury.aggroindicator.network;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.network.packet.MobDeAggroPacket;
import com.github.raverbury.aggroindicator.network.packet.MobTargetPlayerPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AggroIndicator.MODID, "main"),
            () -> PROTOCOL_VERSION,
            version -> PROTOCOL_VERSION.equals(version) || NetworkRegistry.ABSENT.equals(version) || NetworkRegistry.ACCEPTVANILLA.equals(version),
            version -> PROTOCOL_VERSION.equals(version) || NetworkRegistry.ABSENT.equals(version) || NetworkRegistry.ACCEPTVANILLA.equals(version)
    );

    public static void register() {
        int messageId = 0;
        CHANNEL.messageBuilder(MobTargetPlayerPacket.class, messageId++)
                .encoder(MobTargetPlayerPacket::encode)
                .decoder(MobTargetPlayerPacket::new)
                .consumerNetworkThread(MobTargetPlayerPacket::handle)
                .add();

        CHANNEL.messageBuilder(MobDeAggroPacket.class, messageId++)
                .encoder(MobDeAggroPacket::encode)
                .decoder(MobDeAggroPacket::new)
                .consumerNetworkThread(MobDeAggroPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG packet, ServerPlayer serverPlayer) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
    }
}
