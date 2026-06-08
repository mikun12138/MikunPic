package me.mikun.mikunpic.database.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object TagTable : IntIdTable("tag") {
    val name =
        varchar(
            "name",
            128,
        ).uniqueIndex()
}
