package me.mikun.mikunpic.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequest(
    @SerialName("pic_name")
    var picName: String,
    @SerialName("illustrator_name")
    var illustratorName: String? = null,
    @SerialName("tag_names")
    val tagNames: List<String> = emptyList()
)

