package org.sasha0552.ledger.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface PacketSender {
    void sendPacket(Identifier channel, PacketByteBuf buf);
}
