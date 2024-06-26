package com.github.quiltservertools.ledger.network

import com.github.quiltservertools.ledger.config.NetworkingSpec
import com.github.quiltservertools.ledger.config.config
import com.github.quiltservertools.ledger.network.packet.LedgerPacketTypes
import com.github.quiltservertools.ledger.network.packet.Receiver
import com.github.quiltservertools.ledger.network.packet.receiver.HandshakePacketReceiver
import com.github.quiltservertools.ledger.network.packet.receiver.InspectReceiver
import com.github.quiltservertools.ledger.network.packet.receiver.PurgeReceiver
import com.github.quiltservertools.ledger.network.packet.receiver.RollbackReceiver
import com.github.quiltservertools.ledger.network.packet.receiver.SearchReceiver
import org.sasha0552.ledger.networking.PacketSender
import dev.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.sasha0552.ledger.networking.PlayerPacketSender

object Networking {
    // List of players who have a compatible client mod
    private var networkedPlayers = mutableSetOf<ServerPlayerEntity>()
    const val PROTOCOL_VERSION = 3

    init {
        if (config[NetworkingSpec.networking]) {
            register(LedgerPacketTypes.INSPECT_POS.id, InspectReceiver())
            register(LedgerPacketTypes.SEARCH.id, SearchReceiver())
            register(LedgerPacketTypes.HANDSHAKE.id, HandshakePacketReceiver())
            register(LedgerPacketTypes.ROLLBACK.id, RollbackReceiver())
            register(LedgerPacketTypes.PURGE.id, PurgeReceiver())
        }
    }

    private fun register(channel: Identifier, receiver: Receiver) {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, channel) {
                buf: PacketByteBuf,
                context: NetworkManager.PacketContext ->
            run {
                val server = context.player.server!!
                val player = context.player as ServerPlayerEntity
                val handler = player.networkHandler
                val sender = PlayerPacketSender(player)

                receiver.receive(server, player, handler, buf, sender)
            }
        }
    }

    fun ServerPlayerEntity.hasNetworking() = networkedPlayers.contains(this)

    fun ServerPlayerEntity.enableNetworking() = networkedPlayers.add(this)

    fun ServerPlayerEntity.disableNetworking() = networkedPlayers.remove(this)
}

