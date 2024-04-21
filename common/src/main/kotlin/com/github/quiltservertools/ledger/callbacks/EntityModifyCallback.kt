package com.github.quiltservertools.ledger.callbacks

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun interface EntityModifyCallback {
    fun modify(
        world: World,
        pos: BlockPos,
        oldEntityTags: NbtCompound,
        newEntity: Entity,
        itemStack: ItemStack?,
        entityActor: Entity?,
        sourceType: String
    )

    companion object {
        @JvmField
        val EVENT: Event<EntityModifyCallback> =
            EventFactory.createLoop(EntityModifyCallback::class.java)
    }
}
