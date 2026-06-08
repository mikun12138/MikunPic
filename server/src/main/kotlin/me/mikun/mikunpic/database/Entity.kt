package me.mikun.mikunpic.database

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.relation.Pic2TagsTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.TagTable
import me.mikun.mikunpic.database.table.relation.Pic2IllustratorTable
import me.mikun.mikunpic.dto.data.Pic
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class PicEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PicEntity>(PicTable)

    var filename by PicTable.filename

    var hash by PicTable.hash

    val illustrator by IllustratorEntity optionalBackReferencedOn Pic2IllustratorTable

    var tags by TagEntity via Pic2TagsTable

    fun toPic(): Pic = Pic(
        this.filename,
        this.illustrator?.name,
        this.tags.map { it.name },
    )
}

class IllustratorEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<IllustratorEntity>(IllustratorTable)

    var name by IllustratorTable.name

    // TODO:: make platform-keys to unique by not by name

    val pics by PicEntity via Pic2IllustratorTable
}

class TagEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(TagTable)

    var name by TagTable.name

    var pics by PicEntity via Pic2TagsTable
}
