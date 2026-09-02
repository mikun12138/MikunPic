package me.mikun.mikunpic.dto.data

import kotlinx.serialization.Serializable

@Serializable
data class Illustrator(
    val id: Int? = null,
    val name: String,
    val platformKeyMap: Map<Platform, String> = mapOf(),
) {
    companion object {
        val UnExist = Illustrator(id = -1, name = "")
    }
}

// fanbox? 抱歉没有那种东西
enum class Platform(
    val value: String,
) {
    Other(""),
    Pixiv("pixiv"),
    Twitter("twitter"),
    ;

    fun buildLink(key: String): String {
        return when (this) {
            Pixiv -> {
                val id = key.substringBefore("_")
                "https://www.pixiv.net/artworks/$id"
                // TODO:: fetch date and the direct link
            }

            Twitter -> {
                val id = key.substringBefore(".")
                val format = key.substringAfterLast(".").let {
                    return@let when (it) {
                        "jpg" -> "jpg"

                        "jpeg" -> "jpg"

                        // windows save as it
                        "jfif" -> "jpg"

                        "png" -> "png"

                        else -> ""
                    }
                }
                "https://pbs.twimg.com/media/$id?format=$format&name=orig"
            }

            Other -> {
                ""
            }
        }
    }

    companion object {
        fun byName(name: String): Platform? = entries.find { it.value == name }
    }
}
