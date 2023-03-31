package com.chow.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object EventBus {
    private val eventMutableLiveData = MutableLiveData<Event>()
    private val eventLiveData = eventMutableLiveData
    var hasNewEvent = false

    fun getLiveData(): LiveData<Event> {
        hasNewEvent = false
        return eventLiveData
    }

    fun emitEvent(event: Event) {
        hasNewEvent = true
        eventMutableLiveData.postValue(event)
    }
}