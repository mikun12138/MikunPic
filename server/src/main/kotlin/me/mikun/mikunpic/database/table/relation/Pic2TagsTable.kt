package me.mikun.mikunpic.database.table.relation

import me.mikun.mikunpic.database.table.MMIntIdTable
import me.mikun.mikunpic.database.table.PicTable
import me.mikun.mikunpic.database.table.TagTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object Pic2TagsTable : MMIntIdTable<EntityID<Int>, EntityID<Int>>("pics2tags") {

    override val c1 =
        reference(
            "pic_id",
            PicTable,
            onDelete = ReferenceOption.CASCADE,
        )
    val picId
        get() = c1

    override val c2 =
        reference(
            "tag_id",
            TagTable,
            onDelete = ReferenceOption.CASCADE,
        )
    val tagId
        get() = c2
}
