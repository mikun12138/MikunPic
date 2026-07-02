package me.mikun.mikunpic.dto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MikunPicConfig(
    @SerialName("storage")
    val storage: Storage? = null,
    @SerialName("auth")
    val auth: Auth? = null,
) {

    @Serializable
    sealed class Storage {
        @Serializable
        @SerialName("local")
        data class Local(
            @SerialName("path")
            val path: String,
        ) : Storage()

        @Serializable
        @SerialName("cos")
        data class Cos(
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