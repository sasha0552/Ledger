package com.github.quiltservertools.ledger.network.packet.response

import com.github.quiltservertools.ledger.network.packet.LedgerPacket
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import io.netty.buffer.Unpooled
import org.sasha0552.ledger.networking.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ResponsePacket : LedgerPacket<ResponseContent> {
    override var buf: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
    override val channel: Identifier = LedgerPacketTypes.RESPONSE.id
    override fun populate(content: ResponseContent) {
        // Packet type, rollback response would be `ledger.rollback`
        buf.writeIdentifier(content.type)
        // Response code
        buf.writeInt(content.response)
    }

    companion object {
        fun sendResponse(content: ResponseContent, sender: PacketSender) {
            val response = ResponsePacket()
            response.populate(content)
            sender.sendPacket(LedgerPacketTypes.RESPONSE.id, response.buf)
        }
    }
}
