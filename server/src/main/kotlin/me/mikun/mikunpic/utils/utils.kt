package me.mikun.mikunpic.utils

fun Iterable<String>.mapToNullable(): List<String?> = this.map { it.ifEmpty { null } }
