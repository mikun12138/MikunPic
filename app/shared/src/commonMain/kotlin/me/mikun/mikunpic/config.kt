package me.mikun.mikunpic

import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.Serializable

private val settings = Settings()

var LocalConfig
    get() = settings.decodeValue<Config>("me.mikun.mikunpic.config", Config())
    set(value) {
        settings.encodeValue("me.mikun.mikunpic.config", value)
    }

@Serializable
data class Config(
    val server: String = "http://127.0.0.1:8080",
    val bg: Bg = Bg(
        home = Bg.Home(),
        manage = Bg.Manage(),
    ),
) {
    @Serializable
    data class Bg(
        val home: Home,
        val manage: Manage,
    ) {
        @Serializable
        data class Home(
            val api: String = "https://mikunpic-1324551995.cos.ap-shanghai.myqcloud.com/78544928_p0.jpg",
        )

        @Serializable
        data class Manage(
            val upload: String = "https://mikunpic-1324551995.cos.ap-shanghai.myqcloud.com/93291422_p0.jpg",
        )
    }
}
