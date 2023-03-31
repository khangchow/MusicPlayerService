package com.chow.service

open class Event(open val key: String)

data class ValueEvent<T>(
    override val key: String,
    val value: T
) : Event(key)
