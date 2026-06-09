package me.mikun.mikunpic.database.table.relation

import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.TagTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Pic2TagsTable : IntIdTable("pics2tags") {

    val picId =
        reference(
            "pic_id",
            PicTable,
            onDelete = ReferenceOption.CASCADE,
        )

    val tagId =
        reference(
            "tag_id",
            TagTable,
            onDelete = ReferenceOption.CASCADE,
        )
}
