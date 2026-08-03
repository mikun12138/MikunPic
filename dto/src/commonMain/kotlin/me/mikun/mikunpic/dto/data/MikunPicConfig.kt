package me.mikun.mikunpic.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MikunPicConfig constructor(
    @SerialName("storage")
    val storages: List<Storage> = emptyList(),
    @SerialName("auth")
    val auth: Auth? = null,
) {

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
            val secretId: String,
            @SerialName("secret_key")
            val secretKey: String,
            @SerialName("bucket_name")
            val bucketName: String,
            @SerialName("region")
            val region: String,
        ) : Storage()
    }

    @Serializable
    sealed class Auth {
        @Serializable
        @SerialName("bearer")
        data class Bearer(
            @SerialName("token")
            val token: String,
        ) : Auth()
    }
}