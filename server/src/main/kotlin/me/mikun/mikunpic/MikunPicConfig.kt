package me.mikun.mikunpic

import kotlinx.serialization.decodeFromString
import me.mikun.mikunpic.dto.data.MikunPicConfig
import net.mamoe.yamlkt.Yaml
import java.io.File

@Suppress("ktlint:standard:backing-property-naming")
private var _LocalMikunPicConfig: MikunPicConfig? = null

private val configFile: File by lazy {
    File(ServerAppDirs.current.config, "config.yaml")
}

var LocalMikunPicConfig: MikunPicConfig
    get() {
        if (_LocalMikunPicConfig == null) {
            if (!configFile.exists()) {
                configFile.parentFile.mkdirs()
                configFile.writeText(
                    Yaml.encodeToString(MikunPicConfig.Def),
                )
            } else {
                require(configFile.isFile) {
                    "Config path is not a file: ${configFile.absolutePath}"
                }
            }

            _LocalMikunPicConfig = runCatching {
                Yaml.decodeFromString<MikunPicConfig>(
                    configFile.readText()
                )
            }.getOrElse {
                it.printStackTrace()
                MikunPicConfig.Def
            }
        }

        return _LocalMikunPicConfig!!
    }
    set(value) {
        configFile.parentFile.mkdirs()
        configFile.writeText(Yaml.encodeToString(value))
        _LocalMikunPicConfig = value
    }
