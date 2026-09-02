package me.mikun.mikunpic.database.table.relation

import me.mikun.mikunpic.database.table.PicTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Pic2TagsTable : IntIdTable("pics2tags") {

    val picId =
        reference(
            "pic_id",
            PicTable,
        )

    val tagId =
        integer("tag_id")

    init {
        uniqueIndex(picId, tagId)
    }
}
