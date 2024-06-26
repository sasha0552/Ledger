package com.github.quiltservertools.ledger.callbacks

import com.github.quiltservertools.ledger.utility.Sources
import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun interface BlockPlaceCallback {
    fun place(
        world: World,
        pos: BlockPos,
        state: BlockState,
        entity: BlockEntity?,
        source: String,
        player: PlayerEntity?
    )

    fun place(world: World, pos: BlockPos, state: BlockState, entity: BlockEntity?, player: PlayerEntity) =
        place(world, pos, state, entity, Sources.PLAYER, player)

    fun place(world: World, pos: BlockPos, state: BlockState, entity: BlockEntity?, source: String) =
        place(world, pos, state, entity, source, null)

    companion object {
        @JvmField
        val EVENT: Event<BlockPlaceCallback> =
            EventFactory.createLoop(BlockPlaceCallback::class.java)
    }
}
