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
    @SerialName("store_key")
    val storeKey: String? = null,
)

@Serializable
data class PicSelect(
    @SerialName("id")
    val id: Int,
    @SerialName("filename")
    val filename: String,
    @SerialName("platform")
    val platform: String,
    @SerialName("store_key")
    val storeKey: String,
)

// TODO::
class PicCreate
class PicUpload