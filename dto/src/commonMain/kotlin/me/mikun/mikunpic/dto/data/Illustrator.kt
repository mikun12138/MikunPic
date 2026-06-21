package me.mikun.mikunpic.dto.data

import kotlinx.serialization.Serializable

@Serializable
data class Illustrator(
    val id: Int?,
    val name: String?,
    val platformKeyMap: Map<Platform, String>,
)

enum class Platform {
    Pixiv,
    Twitter,
}