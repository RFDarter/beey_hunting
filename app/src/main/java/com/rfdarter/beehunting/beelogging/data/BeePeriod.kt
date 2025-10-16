package com.rfdarter.beehunting.beelogging.data

data class BeePeriod(
    val fromEvent: BeeEvent,
    val toEvent: BeeEvent,
    val duration: Long // in Millisekunden
)