package com.rfdarter.beehunting

enum class EventType {
    ArrivedAtFeeder,
    LeftFeederToHive
}

data class BeeEvent(
    val eventType: EventType,
    val timestamp: Long,
    val flightDirection: Float? = null
)