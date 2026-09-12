package me.mikun.mikunpic.database.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object PlatformKeyTable : IntIdTable("platform_key") {
    val platform =
        varchar(
            "platform",
            32,
        )

    val key =
        varchar(
            "key",
            128,
        )

    init {
        uniqueIndex(platform, key)
    }
}
