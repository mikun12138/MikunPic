package me.mikun.mikunpic.database

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

    var illustrator by IllustratorEntity optionalReferencedOn PicTable.illustratorId

    var tags by TagEntity via Pic2TagTable

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

    val pics by PicEntity optionalReferrersOn PicTable.illustratorId
}

class TagEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<TagEntity>(TagTable)

    var name by TagTable.name

    var pics by PicEntity via Pic2TagTable
}
