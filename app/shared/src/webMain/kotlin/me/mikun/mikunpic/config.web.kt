package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.io.files.Path
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import org.w3c.fetch.RequestInit

private val configFile = Path(ClientAppDirs.config, "config.yaml").toString()

@Composable
actual fun LoadConfig(): Config {
    val config by produceState(initialValue = Config.Def) {
        value = runCatching {
            val init = js("{}").unsafeCast<RequestInit>()
            val response = window.fetch(configFile, init).await()

            require(response.ok)

            val text = response.text().await()
            require(!text.trimStart().startsWith("<")) {
                "/config/config.yaml is missing or returned HTML"
            }

            Yaml.decodeFromString<Config>(text)
        }.getOrElse { e ->
            e.printStackTrace()
            Config.Def
        }
    }

    return config
}
