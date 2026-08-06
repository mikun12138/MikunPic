package me.mikun.mikunpic.dto.data

import kotlinx.serialization.Serializable

@Serializable
data class Illustrator(
    val id: Int? = null,
    val name: String,
    val platformKeyMap: Map<Platform, String> = mapOf(),
) {
    companion object {
        val UnExist = Illustrator(name = "")
    }
}

// fanbox? 抱歉没有那种东西
enum class Platform {
    Other,
    Pixiv,
    Twitter,
}
