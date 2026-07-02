package me.mikun.mikunpic

import net.mamoe.yamlkt.Yaml
import kotlin.test.Test

class SharedCommonTest {
    @Test
    fun test() {
        println(Yaml().encodeToString(Config(server = "127.0.0.1:8080")))
    }
}
