package com.github.quiltservertools.ledger.callbacks

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun interface BlockMeltCallback {
    fun melt(world: World, pos: BlockPos, oldState: BlockState, newState: BlockState, entity: BlockEntity?)

    companion object {
        @JvmField
        val EVENT: Event<BlockMeltCallback> =
            EventFactory.createLoop(BlockMeltCallback::class.java)
    }
}
