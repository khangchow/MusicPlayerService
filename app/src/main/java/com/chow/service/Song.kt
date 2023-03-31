package com.chow.service

import java.io.Serializable

data class Song(
    val title: String = "",
    val singer: String = "",
    val image: Int = 0,
    val resource: Int = 0
) : Serializable