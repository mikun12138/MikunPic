package me.mikun.mikunpichost.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object PicTable : IntIdTable("pic") {
    val filename = varchar(
        "filename",
        128
    ).uniqueIndex()
    val hash = char(
        "hash",
        32
    ).uniqueIndex()

    val illustratorId = reference(
        "illustrator_id",
        IllustratorTable
    ).nullable()
}

object IllustratorTable : IntIdTable("illustrator") {
    val name = varchar(
        "name",
        128
    ).uniqueIndex()
}

object TagTable : IntIdTable("tag") {
    val name = varchar(
        "name",
        128
    ).uniqueIndex()
}

object Pic2TagTable : Table("pic2tag") {
    val pic = reference(
        "pic_id",
        PicTable,
        onDelete = ReferenceOption.CASCADE
    )
    val tag = reference(
        "tag_id",
        TagTable,
        onDelete = ReferenceOption.CASCADE
    )

    override val primaryKey = PrimaryKey(
        pic,
        tag
    )
}

