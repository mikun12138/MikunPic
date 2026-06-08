package me.mikun.mikunpic.database.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object PicTable : IntIdTable("pic") {
    val filename =
        varchar(
            "filename",
            128,
        ).uniqueIndex()
    val hash =
        char(
            "hash",
            32,
        ).uniqueIndex()
}
