package me.mikun.mikunpic

import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabase() {
    Database.connect("jdbc:sqlite:./data/databases/pic.db", driver = "org.sqlite.JDBC")
}
