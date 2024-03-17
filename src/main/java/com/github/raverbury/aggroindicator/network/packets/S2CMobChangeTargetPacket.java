package com.github.raverbury.aggroindicator.network.packets;

import com.github.raverbury.aggroindicator.AggroIndicator;
import com.github.raverbury.aggroindicator.AlertRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record S2CMobChangeTargetPacket(UUID mobUuid, boolean playerIsNewTarget) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(AggroIndicator.MODID, "mob_target_player_packet");

    public S2CMobChangeTargetPacket(final FriendlyByteBuf pBuffer) {
        this(pBuffer.readUUID(), pBuffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(mobUuid());
        pBuffer.writeBoolean(playerIsNewTarget());
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public static void handle(final S2CMobChangeTargetPacket packet, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            if (packet.playerIsNewTarget) {
                AlertRenderer.addMobTargetingThisClientPlayer(packet.mobUuid);
            } else {
                AlertRenderer.removeMobTargetingThisClientPlayer(packet.mobUuid);
            }
        });
    }
}