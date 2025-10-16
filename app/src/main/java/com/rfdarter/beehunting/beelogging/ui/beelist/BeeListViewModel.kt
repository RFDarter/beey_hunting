package com.rfdarter.beehunting.beelogging.ui.beelist

import androidx.lifecycle.ViewModel
import com.rfdarter.beehunting.beelogging.data.BeeColor
import com.rfdarter.beehunting.beelogging.data.BeeEvent
import com.rfdarter.beehunting.beelogging.data.HoneyBee
import com.rfdarter.beehunting.beelogging.data.HoneyBeeFactory

class BeeListViewModel : ViewModel() {
    fun AddNewBee(color: BeeColor): HoneyBee? {
        return HoneyBeeFactory.createBee(color)
    }

    fun OnBeeArrivedPressed(bee: HoneyBee) {
        bee.addEvent(BeeEvent(eventType = BeeEvent.EventType.ArrivedAtFeeder, timestamp = System.currentTimeMillis()))
    }

    fun OnBeeLeftPressed(bee: HoneyBee) {
        bee.addEvent(BeeEvent(eventType = BeeEvent.EventType.DepartedFromFeeder, timestamp = System.currentTimeMillis()))
    }

    fun DeleteBee(bee: HoneyBee): Boolean = HoneyBeeFactory.deleteBee(bee)
}