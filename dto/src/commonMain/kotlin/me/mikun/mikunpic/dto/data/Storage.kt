package me.mikun.mikunpic.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Storage {
    @SerialName("label")
    abstract val label: String

    @Serializable
    @SerialName("local")
    data class Local(
        @SerialName("label")
        override val label: String,
        @SerialName("path")
        val path: String,
    ) : Storage()

    @Serializable
    @SerialName("cos")
    data class Cos(
        @SerialName("label")
        override val label: String,
        @SerialName("secret_id")
        val secretId: String? = null,
        @SerialName("secret_key")
        val secretKey: String? = null,
        @SerialName("bucket_name")
        val bucketName: String,
        @SerialName("region")
        val region: String,
    ) : Storage()
}