package me.mikun.mikunpic

import kotlinx.serialization.decodeFromString
import me.mikun.mikunpic.dto.data.MikunPicConfig
import net.mamoe.yamlkt.Yaml
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("ktlint:standard:backing-property-naming")
private var _LocalMikunPicConfig: MikunPicConfig? = null

private val configFile = File("config.yaml")

@OptIn(ExperimentalUuidApi::class)
var LocalMikunPicConfig: MikunPicConfig
    get() {
        if (_LocalMikunPicConfig == null) {
            if (!configFile.exists() || !configFile.isFile) {
                configFile.createNewFile()
                configFile.writeText(
                    Yaml.encodeToString(MikunPicConfig.Def),
                )
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
        configFile.apply {
            exists() || createNewFile()
        }.writeText(
            Yaml.encodeToString(value),
        )
        _LocalMikunPicConfig = value
    }
