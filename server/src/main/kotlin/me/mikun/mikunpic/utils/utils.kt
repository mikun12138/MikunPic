package me.mikun.mikunpic.utils

fun Iterable<String>.mapToNullable(): List<String?> {
    return this.map { it.ifEmpty { null } }
}

