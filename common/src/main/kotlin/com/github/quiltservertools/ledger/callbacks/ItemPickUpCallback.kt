package com.github.quiltservertools.ledger.callbacks

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity

fun interface ItemPickUpCallback {
    fun pickUp(entity: ItemEntity, player: PlayerEntity)

    companion object {
        @JvmField
        val EVENT: Event<ItemPickUpCallback> =
            EventFactory.createLoop(ItemPickUpCallback::class.java)
    }
}
