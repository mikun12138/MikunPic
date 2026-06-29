package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val LocalConfig = staticCompositionLocalOf<Config> {
    error("LocalConfig not provided")
}

@Composable
expect fun LoadConfig(): Config

@Serializable
data class Config(
    @SerialName("server")
    val server: String = "",
) {
    companion object {
        val Def = Config()
    }
}