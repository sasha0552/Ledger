package org.sasha0552.ledger.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerPacketSender implements PacketSender {
    private final ServerPlayerEntity player;

    public PlayerPacketSender(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void sendPacket(Identifier channel, PacketByteBuf buf) {
        NetworkManager.sendToPlayer(player, channel, buf);
    }
}
