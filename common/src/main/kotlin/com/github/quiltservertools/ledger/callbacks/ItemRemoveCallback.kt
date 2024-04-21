package com.github.quiltservertools.ledger.callbacks

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

fun interface ItemRemoveCallback {
    fun remove(stack: ItemStack, pos: BlockPos, world: ServerWorld, source: String, player: ServerPlayerEntity?)

    companion object {
        @JvmField
        val EVENT: Event<ItemRemoveCallback> =
            EventFactory.createLoop(ItemRemoveCallback::class.java)
    }
}
