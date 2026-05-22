package me.mikun.mikunpic.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.mikun.mikunpic.dto.data.Pic

@Serializable
data class SelectResponse(
    @SerialName("pic")
    val pic: Pic
)
