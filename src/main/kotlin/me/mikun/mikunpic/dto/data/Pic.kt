package me.mikun.mikunpic.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.mikun.mikunpic.database.PicEntity

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
        fun PicEntity.toPic(): Pic {
            return Pic(
                this.filename,
                this.illustrator?.name,
                this.tags.map { it.name }
            )
        }
    }
}
