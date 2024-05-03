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
import dev.architectury.event.EventResult
import kotlinx.coroutines.launch
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.BlockEvent
import org.sasha0552.ledger.networking.PacketSender
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.utils.value.IntValue
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
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

fun onLeave(player: ServerPlayerEntity) {
    player.disableNetworking()
}

private fun onUseBlock(
    player: PlayerEntity,
    hand: Hand,
    pos: BlockPos,
    direction: Direction
): EventResult {
    if (player.isInspecting() && hand == Hand.MAIN_HAND) {
        player.commandSource.inspectBlock(pos.offset(direction))
        return EventResult.interruptTrue()
    }

    return EventResult.pass()
}

private fun onBlockAttack(
    player: PlayerEntity,
    hand: Hand,
    pos: BlockPos,
    direction: Direction
): EventResult {
    if (player.world.isClient) return EventResult.pass()

    if (player.isInspecting()) {
        player.commandSource.inspectBlock(pos)
        return EventResult.interruptTrue()
    }

    return EventResult.pass()
}


private fun onJoin(player: ServerPlayerEntity) {
    Ledger.launch {
        DatabaseManager.logPlayer(player.uuid, player.entityName)
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
    world: World?,
    pos: BlockPos?,
    state: BlockState?,
    player: ServerPlayerEntity?,
    xp: IntValue?
): EventResult? {
    ActionQueueService.addToQueue(
        ActionFactory.blockBreakAction(
            world!!,
            pos!!,
            state!!,
            player!!,
            world.getBlockEntity(pos)
        )
    )
    return EventResult.pass()
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
