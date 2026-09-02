package me.mikun.mikunpic.database

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.PlatformKeyTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Illustrator2PlatformKeysTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import me.mikun.mikunpic.dto.data.Pic
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.SizedCollection

class PicEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PicEntity>(PicTable)

    var filename by PicTable.filename

    var hash by PicTable.hash
}

class IllustratorEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<IllustratorEntity>(IllustratorTable)

    var name by IllustratorTable.name

    var platformKeys by PlatformKeyEntity via Illustrator2PlatformKeysTable
}

class PlatformKeyEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PlatformKeyEntity>(PlatformKeyTable)

    var platform by PlatformKeyTable.platform
    var key by PlatformKeyTable.key
}

class TagEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(TagTable)

    var name by TagTable.name
}
