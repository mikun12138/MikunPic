package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml

@Composable
actual fun LoadConfig(): Config {
    val context = LocalContext.current.applicationContext
    val config by produceState(initialValue = Config.Def, context) {
        value = runCatching {
            val text = context.assets.open("config.yaml").bufferedReader(Charsets.UTF_8).use {
                it.readText()
            }
            Yaml.decodeFromString<Config>(text)
        }.getOrElse { e ->
            e.printStackTrace()
            Config.Def
        }
    }

    return config
}
