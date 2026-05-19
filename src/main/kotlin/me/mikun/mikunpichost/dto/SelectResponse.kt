package me.mikun.mikunpichost.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.mikun.me.mikun.mikunpichost.dto.data.Pic
import me.mikun.mikunpichost.database.PicEntity

@Serializable
data class SelectResponse(
    @SerialName("pic")
    val pic: Pic
)
