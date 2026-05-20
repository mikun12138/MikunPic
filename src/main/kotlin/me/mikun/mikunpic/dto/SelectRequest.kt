package me.mikun.mikunpic.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SelectRequest(
    @SerialName("filename")
    val filename: String
)
