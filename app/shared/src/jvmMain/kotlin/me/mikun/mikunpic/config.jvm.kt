package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import java.io.File

@Composable
actual fun LoadConfig(): Config {
    val config by produceState(initialValue = Config.Def) {
        value = runCatching {
            val text = File("config.yaml").readText()
            Yaml.decodeFromString<Config>(text)
        }.getOrElse { e ->
            e.printStackTrace()
            Config.Def
        }
    }

    return config
}
