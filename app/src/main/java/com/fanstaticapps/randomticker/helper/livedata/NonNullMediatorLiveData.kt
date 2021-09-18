package com.fanstaticapps.randomticker.helper.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class NonNullMediatorLiveData<T> : MediatorLiveData<T>()

fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator = NonNullMediatorLiveData<T>()
    mediator.addSource(this) { it?.let { mediator.value = it } }
    return mediator
}