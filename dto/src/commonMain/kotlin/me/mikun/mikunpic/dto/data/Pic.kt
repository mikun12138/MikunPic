package me.mikun.mikunpic.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pic constructor(
    @SerialName("filename")
    val filename: String,
    @SerialName("illustrator")
    val illustrator: Illustrator?,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
)
