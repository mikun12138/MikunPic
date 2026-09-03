package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val LocalConfig = staticCompositionLocalOf<Config> {
    error("LocalConfig not provided")
}

const val CONFIG_FILE_NAME = "config.yaml"

@Composable
expect fun LoadConfig(): Config

@Serializable
data class Config(
    @SerialName("server")
    val server: String = "http://127.0.0.1:8080",
    @SerialName("preview_api")
    val previewApi: String = "/random",
    @SerialName("apis")
    val apis: List<Api> = listOf(
        Api(
            name = "-随机图-",
            url = "http://127.0.0.1:8080/random",
        ),
        Api(
            name = "+随机图-",
            url = "http://127.0.0.1:8080/random",
        ),
        Api(
            name = "-随机图+",
            url = "http://127.0.0.1:8080/random",
        ),
        Api(
            name = "+随机图+",
            url = "http://127.0.0.1:8080/random",
        ),
    ),
) {

    @Serializable
    data class Api(
        @SerialName("name")
        val name: String,
        @SerialName("url")
        val url: String,
    )

    companion object {
        val Def = Config()
    }
}
