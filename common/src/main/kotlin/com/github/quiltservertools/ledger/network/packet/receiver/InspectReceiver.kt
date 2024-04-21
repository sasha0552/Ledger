package com.github.quiltservertools.ledger.network.packet.receiver

import com.github.quiltservertools.ledger.Ledger
import com.github.quiltservertools.ledger.commands.CommandConsts
import com.github.quiltservertools.ledger.database.DatabaseManager
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import com.github.quiltservertools.ledger.network.packet.Receiver
import com.github.quiltservertools.ledger.network.packet.action.ActionPacket
import com.github.quiltservertools.ledger.network.packet.response.ResponseCodes
import com.github.quiltservertools.ledger.network.packet.response.ResponseContent
import com.github.quiltservertools.ledger.network.packet.response.ResponsePacket
import com.github.quiltservertools.ledger.utility.getInspectResults
import kotlinx.coroutines.launch
import org.sasha0552.ledger.LedgerExpectPlatform
import org.sasha0552.ledger.networking.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

class InspectReceiver : Receiver {
    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        sender: PacketSender
    ) {
        if (!LedgerExpectPlatform.Permissions_check(player, "ledger.networking", CommandConsts.PERMISSION_LEVEL) ||
            !LedgerExpectPlatform.Permissions_check(player, "ledger.commands.inspect", CommandConsts.PERMISSION_LEVEL)
        ) {
            ResponsePacket.sendResponse(ResponseContent(LedgerPacketTypes.INSPECT_POS.id, ResponseCodes.NO_PERMISSION.code), sender)
            return
        }

        val pos = buf.readBlockPos()
        ResponsePacket.sendResponse(ResponseContent(LedgerPacketTypes.INSPECT_POS.id, ResponseCodes.EXECUTING.code), sender)

        val pages = buf.readInt()

        Ledger.launch {
            val results = player.getInspectResults(pos)
            for (i in 1..pages) {
                val page = DatabaseManager.searchActions(results.searchParams, i)
                page.actions.forEach { action ->
                    val packet = ActionPacket()
                    packet.populate(action)
                    sender.sendPacket(packet.channel, packet.buf)
                }
            }
            ResponsePacket.sendResponse(ResponseContent(LedgerPacketTypes.INSPECT_POS.id, ResponseCodes.COMPLETED.code), sender)
        }
    }
}
