package me.mikun.mikunpic.database.table.relation

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PlatformKeyTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Illustrator2PlatformKeysTable : IntIdTable("illustrator2platform_keys") {

    val illustrator =
        reference(
            "illustrator",
            IllustratorTable.id,
        )

    val platformkey =
        reference(
            "platform_key",
            PlatformKeyTable.id,
        ).uniqueIndex()
}
