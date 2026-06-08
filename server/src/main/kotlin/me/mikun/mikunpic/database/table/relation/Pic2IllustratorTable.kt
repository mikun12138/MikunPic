package me.mikun.mikunpic.database.table.relation

import me.mikun.mikunpic.database.table.IllustratorTable
import me.mikun.mikunpic.database.table.PicTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

//object Pic2IllustratorTable : MOIntIdTable<EntityID<Int>, EntityID<Int>>("pic2illustrator") {
//
//    override val c1
//        get() =
//            reference(
//                "pic_id",
//                PicTable,
//                onDelete = ReferenceOption.CASCADE,
//            ).uniqueIndex()
//    val pic
//        get() = c1
//
//    override val c2 =
//        reference(
//            "illustrator_id",
//            IllustratorTable,
//        )
//    val illustratorId
//        get() = c2
//}

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
