package me.mikun.mikunpic

import me.mikun.mikunpic.dto.data.MikunPicConfig
import net.mamoe.yamlkt.Yaml
import kotlin.test.Test

class Test {

    @Test
    fun test() {
        Yaml.encodeToString(
            MikunPicConfig(
                storage = MikunPicConfig.Storage.Local(
                    path = "qwq"
                ),
                auth = MikunPicConfig.Auth.Bearer(
                    token = "qwqwq"
                )
            )
        ).let {
            println(it)
        }
    }
}
