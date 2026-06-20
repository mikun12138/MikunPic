package me.mikun.mikunpic.database.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object PlatformKeyTable: IntIdTable("platform_key") {
    val platform =
        enumeration<Platform>("platform")

    val key =
        varchar(
            "key",
            128
        )

    init {
        uniqueIndex(platform, key)
    }

    enum class Platform {
        PIXIV,
        TWITTER,
    }
}