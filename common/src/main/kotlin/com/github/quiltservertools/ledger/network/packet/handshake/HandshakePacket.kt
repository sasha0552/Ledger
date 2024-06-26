package com.github.quiltservertools.ledger.network.packet.handshake

import com.github.quiltservertools.ledger.network.packet.LedgerPacket
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class HandshakePacket: LedgerPacket<HandshakeContent> {
    override val channel: Identifier = LedgerPacketTypes.HANDSHAKE.id
    override var buf: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
    override fun populate(content: HandshakeContent) {
        // Ledger information
        // Protocol Version
        buf.writeInt(content.protocolVersion)

        // Ledger Version
        buf.writeString(content.ledgerVersion)

        // We tell the client mod how many actions we are writing
        buf.writeInt(content.actions.size)

        for (action in content.actions) {
            buf.writeString(action)
        }
    }
}
