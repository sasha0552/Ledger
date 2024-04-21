package com.github.quiltservertools.ledger.callbacks

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity

fun interface ItemDropCallback {
    fun drop(entity: ItemEntity, player: PlayerEntity)

    companion object {
        @JvmField
        val EVENT: Event<ItemDropCallback> = EventFactory.createLoop(ItemDropCallback::class.java)
    }
}
