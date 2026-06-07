package me.mikun.mikunpic.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pic(
    @SerialName("filename")
    val filename: String,
    @SerialName("illustrator")
    val illustrator: String?,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
)
