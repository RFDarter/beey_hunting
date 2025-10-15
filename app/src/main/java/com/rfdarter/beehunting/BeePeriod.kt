package com.rfdarter.beehunting

data class BeePeriod(
    val fromEvent: BeeEvent,
    val toEvent: BeeEvent,
    val duration: Long // in Millisekunden
)