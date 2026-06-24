package me.mikun.mikunpic.database.table.relation

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Pic2IllustratorTable : IntIdTable("pic2illustrator") {

    val picId =
        reference(
            "pic_id",
            PicTable,
            onDelete = ReferenceOption.CASCADE,
        ).uniqueIndex()

    val illustratorId =
        reference(
            "illustrator_id",
            IllustratorTable,
        ).nullable()
}
