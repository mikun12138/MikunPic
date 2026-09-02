package me.mikun.mikunpic.modules

import io.ktor.server.application.Application
import me.mikun.mikunpic.LocalMikunPicConfig
import me.mikun.mikunpic.ServerAppDirs
import me.mikun.mikunpic.database.MetadataDB
import me.mikun.mikunpic.database.StorageDB
import org.jetbrains.exposed.v1.jdbc.Database
import java.io.File

fun Application.configureDatabase() {
    val databaseDir = File(ServerAppDirs.current.data, "databases")
    databaseDir.mkdirs()

    MetadataDB.init(
        Database.connect(
            "jdbc:sqlite:${File(databaseDir, "metadata.db").path}",
            driver = "org.sqlite.JDBC",
        ),
    )

    val storageDatabaseDir = File(databaseDir, "storage")
    storageDatabaseDir.mkdirs()

    StorageDB.dbs.addAll(
        LocalMikunPicConfig.storages.map {
            StorageDB(
                Database.connect(
                    "jdbc:sqlite:${File(storageDatabaseDir, "${it.label}.db").path}",
                    driver = "org.sqlite.JDBC",
                ),
            )
        },
    )
}
