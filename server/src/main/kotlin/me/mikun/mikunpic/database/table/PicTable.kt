package me.mikun.mikunpic.database.table

import me.mikun.mikunpic.dto.data.Platform
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object PicTable : IntIdTable("pic") {
    val hash =
        char(
            "hash",
            32,
        ).uniqueIndex()

    val filename =
        varchar(
            "filename",
            128,
        )

    val platform =
        enumeration<Platform>("platform")

    val storeKey =
        varchar(
            "store_key",
            512,
        )

    val link =
        char(
            "link",
            2048,
        ).default("")
}
