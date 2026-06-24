package me.mikun.mikunpic.database.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object IllustratorTable : IntIdTable("illustrator") {
    val name =
        varchar(
            "name",
            128,
        )
}
