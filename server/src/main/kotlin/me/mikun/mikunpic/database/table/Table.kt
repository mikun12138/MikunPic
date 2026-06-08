package me.mikun.mikunpic.database.table

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

@Deprecated("maybe no use")
abstract class OOIntIdTable(
    name: String,
) : IntIdTable(name = name)

abstract class MOIntIdTable<C1, C2>(
    name: String,
) : IntIdTable(name = name) {
    protected abstract val c1: Column<C1>
    protected abstract val c2: Column<C2>

    init {
//        uniqueIndex(c1)
    }
}

abstract class MMIntIdTable<C1, C2>(
    name: String,
) : IntIdTable(name = name) {
    protected abstract val c1: Column<C1>
    protected abstract val c2: Column<C2>

    init {
//        uniqueIndex(c1, c2)
    }
}
