package com.rfdarter.beehunting.beelogging.data



data class BeeEvent(
    val eventType: EventType,
    val timestamp: Long,
    val flightDirection: Float? = null
)
{
    enum class EventType {
        ArrivedAtFeeder,
        DepartedFromFeeder,
        DepartedToHive,
        ArrivedAtHive,
        Unknown
    }}