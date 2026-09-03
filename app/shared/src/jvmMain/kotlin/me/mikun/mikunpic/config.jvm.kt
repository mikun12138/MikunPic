package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.serialization.decodeFromString
import net.harawata.appdirs.AppDirsFactory
import net.mamoe.yamlkt.Yaml
import java.io.File


@Composable
actual fun LoadConfig(): Config {
    val config by produceState(initialValue = Config.Def) {
        value = loadJvmConfig()
    }

    return config
}

private fun loadJvmConfig(): Config {
    if (!userConfigFile.exists()) {
        userConfigFile.parentFile.mkdirs()
        userConfigFile.createNewFile()
        userConfigFile.writeText(Yaml.encodeToString(Config.Def))
    }

    runCatching {
        Yaml.decodeFromString<Config>(userConfigFile.readText())
    }.onSuccess {
        return it
    }

    return Config.Def
}

private val userConfigFile: File by lazy {
    val configDir = AppDirsFactory.getInstance()
        .getUserConfigDir(APP_NAME, null, null)

    File(configDir, CONFIG_FILE_NAME)
}
