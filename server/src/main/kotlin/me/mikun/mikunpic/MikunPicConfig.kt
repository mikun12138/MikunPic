package me.mikun.mikunpic

import io.ktor.server.application.log
import kotlinx.serialization.decodeFromString
import me.mikun.mikunpic.dto.data.MikunPicConfig
import net.mamoe.yamlkt.Yaml
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("ktlint:standard:backing-property-naming")
private var _LocalMikunPicConfig: MikunPicConfig? = null

@OptIn(ExperimentalUuidApi::class)
var LocalMikunPicConfig: MikunPicConfig
    get() {
        if (_LocalMikunPicConfig == null) {
            _LocalMikunPicConfig = runCatching {
                Yaml.decodeFromString<MikunPicConfig>(
                    File("config.yaml").readText(),
                )
            }.getOrElse {
                it.printStackTrace()
                MikunPicConfig(
                    auth = MikunPicConfig.Auth.Bearer(
                        token = Uuid.random().toString(),
                    ),
                )
            }
        }

        return _LocalMikunPicConfig!!
    }
    set(value) {
        File("config.yaml").apply {
            exists() || createNewFile()
        }.writeText(
            Yaml.encodeToString(value),
        )
        _LocalMikunPicConfig = value
    }
