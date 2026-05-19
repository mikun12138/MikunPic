package me.mikun.me.mikun.mikunpichost.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.mikun.mikunpichost.database.PicEntity

@Serializable
data class Pic(
    @SerialName("filename")
    val filename: String,
    @SerialName("illustrator")
    val illustrator: String?,
    @SerialName("tags")
    val tags: List<String> = emptyList()
) {
    companion object {
        fun fromPicEntity(
            picEntity: PicEntity
        ): Pic {
            return Pic(
                picEntity.filename,
                picEntity.illustrator?.name,
                picEntity.tags.map { it.name }
            )
        }
    }
}
