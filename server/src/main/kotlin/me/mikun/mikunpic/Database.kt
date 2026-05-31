package me.mikun.mikunpic

import io.ktor.server.application.Application
import me.mikun.mikunpic.database.IllustratorTable
import me.mikun.mikunpic.database.Pic2TagTable
import me.mikun.mikunpic.database.PicTable
import me.mikun.mikunpic.database.TagTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

fun Application.configureDatabase() {
    File("./data/databases").apply {
        exists() || mkdirs()
    }
    Database.connect("jdbc:sqlite:./data/databases/pic.db", driver = "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(
            PicTable,
            IllustratorTable,
            TagTable,
            Pic2TagTable
        )
    }
}
