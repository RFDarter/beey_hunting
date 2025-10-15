package com.rfdarter.beehunting

import kotlin.inc

object HoneyBeeFactory {
    private val bees = mutableListOf<HoneyBee>()
    private var nextId = 1

    fun createBee(color: BeeColor): HoneyBee? {
        val thoraxSet = color.thorax != null
        val abdomenSet = color.abdomen != null
        if (!thoraxSet && !abdomenSet) return null // Beide Farben fehlen, nicht erlaubt
        if (bees.any { it.color == color }) {
            return null // Farb-Kombination existiert schon
        }
        val bee = HoneyBee(id = nextId++, color = color)
        bees.add(bee)
        return bee
    }


    fun getAllBees(): List<HoneyBee> = bees
}