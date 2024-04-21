package com.github.quiltservertools.ledger.listeners

import com.github.quiltservertools.ledger.Ledger
import com.github.quiltservertools.ledger.actionutils.ActionFactory
import com.github.quiltservertools.ledger.callbacks.ItemDropCallback
import com.github.quiltservertools.ledger.callbacks.ItemPickUpCallback
import com.github.quiltservertools.ledger.database.ActionQueueService
import com.github.quiltservertools.ledger.database.DatabaseManager
import com.github.quiltservertools.ledger.network.Networking.disableNetworking
import com.github.quiltservertools.ledger.utility.inspectBlock
import com.github.quiltservertools.ledger.utility.isInspecting
import kotlinx.coroutines.launch
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.BlockEvent
import dev.architectury.event.events.common.InteractionEvent
import org.sasha0552.ledger.networking.PacketSender
import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

fun registerPlayerListeners() {
    BlockEvent.BREAK.register(::onBlockBreak)
    PlayerEvent.PLAYER_JOIN.register(::onJoin)
    PlayerEvent.PLAYER_QUIT.register(::onLeave)
    InteractionEvent.LEFT_CLICK_BLOCK.register(::onBlockAttack)
    InteractionEvent.RIGHT_CLICK_BLOCK.register(::onUseBlock)
    ItemPickUpCallback.EVENT.register(::onItemPickUp)
    ItemDropCallback.EVENT.register(::onItemDrop)
}

fun onLeave(handler: ServerPlayNetworkHandler, server: MinecraftServer) {
    handler.player.disableNetworking()
}

private fun onUseBlock(
    player: PlayerEntity,
    world: World,
    hand: Hand,
    blockHitResult: BlockHitResult
): ActionResult {
    if (player.isInspecting() && hand == Hand.MAIN_HAND) {
        player.commandSource.inspectBlock(blockHitResult.blockPos.offset(blockHitResult.side))
        return ActionResult.SUCCESS
    }

    return ActionResult.PASS
}

private fun onBlockAttack(
    player: PlayerEntity,
    world: World,
    hand: Hand,
    pos: BlockPos,
    direction: Direction
): ActionResult {
    if (world.isClient) return ActionResult.PASS

    if (player.isInspecting()) {
        player.commandSource.inspectBlock(pos)
        return ActionResult.SUCCESS
    }

    return ActionResult.PASS
}


private fun onJoin(networkHandler: ServerPlayNetworkHandler, packetSender: PacketSender, server: MinecraftServer) {
    Ledger.launch {
        DatabaseManager.logPlayer(networkHandler.player.uuid, networkHandler.player.nameForScoreboard)
    }
}

private fun onBlockPlace(
    world: World,
    player: PlayerEntity,
    pos: BlockPos,
    state: BlockState,
    context: ItemPlacementContext?,
    blockEntity: BlockEntity?
) {
    ActionQueueService.addToQueue(
        ActionFactory.blockPlaceAction(
            world,
            pos,
            state,
            player,
            blockEntity
        )
    )
}

private fun onBlockBreak(
    world: World,
    player: PlayerEntity,
    pos: BlockPos,
    state: BlockState,
    blockEntity: BlockEntity?
) {
    ActionQueueService.addToQueue(
        ActionFactory.blockBreakAction(
            world,
            pos,
            state,
            player,
            blockEntity
        )
    )
}

private fun onItemPickUp(
    entity: ItemEntity,
    player: PlayerEntity
) {
    ActionQueueService.addToQueue(ActionFactory.itemPickUpAction(entity, player))
}

private fun onItemDrop(
    entity: ItemEntity,
    player: PlayerEntity
) {
    ActionQueueService.addToQueue(ActionFactory.itemDropAction(entity, player))
}
