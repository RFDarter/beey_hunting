package com.rfdarter.beehunting.beelogging.data

import com.rfdarter.beehunting.beelogging.data.BeeColor

enum class BeeStatus {
    atFeeder,
    atHive,
    flyingtoFeeder,
    flyingtoHive,
}

class HoneyBee(
    val id: Int,
    val color: BeeColor,
    var status: BeeStatus = BeeStatus.atFeeder
) {
    val events: MutableList<BeeEvent> = mutableListOf()
    val feederPeriods: MutableList<BeePeriod> = mutableListOf()
    val awayPeriods: MutableList<BeePeriod> = mutableListOf()

    fun addEvent(event: BeeEvent) {
        events.add(event)
        if (event.eventType == EventType.LeftFeederToHive) {
            this.status = BeeStatus.flyingtoHive
            val lastArrived = events.dropLast(1).lastOrNull { it.eventType == EventType.ArrivedAtFeeder }
            if (lastArrived != null) {
                feederPeriods.add(
                    BeePeriod(
                        lastArrived,
                        event,
                        event.timestamp - lastArrived.timestamp
                    )
                )
            }
        } else if (event.eventType == EventType.ArrivedAtFeeder) {
            this.status = BeeStatus.atFeeder
            val lastLeft = events.dropLast(1).lastOrNull { it.eventType == EventType.LeftFeederToHive }
            if (lastLeft != null) {
                awayPeriods.add(BeePeriod(lastLeft, event, event.timestamp - lastLeft.timestamp))
            }
        }
    }

    fun getAwayDuration(beeEvent: BeeEvent) : Long {
        if (beeEvent.eventType != EventType.ArrivedAtFeeder) {
            throw IllegalArgumentException("Event must be of type ArrivedAtFeeder")
        }
        val awayPeriod: BeePeriod? = awayPeriods.find { it.toEvent == beeEvent }
        return awayPeriod?.duration ?: 0
    }

    fun getFeederDuration(beeEvent: BeeEvent) : Long {
        if (beeEvent.eventType != EventType.LeftFeederToHive) {
            throw IllegalArgumentException("Event must be of type LeftFeederToHive")
        }
        val feederPeriod: BeePeriod? = feederPeriods.find { it.toEvent == beeEvent }
        return feederPeriod?.duration ?: 0
    }
}





