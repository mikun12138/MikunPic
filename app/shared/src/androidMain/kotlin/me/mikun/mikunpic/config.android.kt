package me.mikun.mikunpic

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import java.io.File


@Composable
actual fun LoadConfig(): Config {
    val context = LocalContext.current.applicationContext
    val userConfigFile = userConfigFile(context)
    val config by produceState(initialValue = Config.Def, context) {
        value = runCatching {
            if (!userConfigFile.exists()) {
                userConfigFile.parentFile?.mkdirs()
                userConfigFile.createNewFile()
                userConfigFile.writeText(Yaml.encodeToString(Config.Def))
            }

            Yaml.decodeFromString<Config>(userConfigFile.readText())
        }.getOrElse { e ->
            e.printStackTrace()
            Config.Def
        }
    }

    return config
}

private fun userConfigFile(context: Context): File {
    return File(File(context.filesDir, APP_NAME), CONFIG_FILE_NAME)
}
