package com.github.quiltservertools.ledger.callbacks

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun interface EntityKillCallback {
    fun kill(world: World, pos: BlockPos, entity: Entity, source: DamageSource)

    companion object {
        @JvmField
        val EVENT: Event<EntityKillCallback> =
            EventFactory.createLoop(EntityKillCallback::class.java)
    }
}
