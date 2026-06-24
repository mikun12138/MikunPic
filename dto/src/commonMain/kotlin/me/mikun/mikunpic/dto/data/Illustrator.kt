package me.mikun.mikunpic.dto.data

import kotlinx.serialization.Serializable

@Serializable
data class Illustrator(
    val id: Int? = null,
    val name: String?,
    val platformKeyMap: Map<Platform, String> = mapOf(),
) {
    companion object {
        val UnExist = Illustrator(name = null)
    }
}

enum class Platform {
    Pixiv,
    Twitter,
}
