package me.mikun.mikunpic.utils

import me.mikun.mikunpic.dto.data.MikunPicConfig
import me.mikun.mikunpic.dto.data.Storage

fun Iterable<String>.mapToNullable(): List<String?> = this.map { it.ifEmpty { null } }

fun Storage.toStorageConfig(): MikunPicConfig.Storage {
    return when (this) {
        is Storage.Local -> {
            MikunPicConfig.Storage.Local(
                label = this.label,
                path = this.path
            )
        }

        is Storage.Cos -> {
            MikunPicConfig.Storage.Cos(
                label = this.label,
                secretId = this.secretId,
                secretKey = this.secretKey,
                bucketName = this.bucketName,
                region = this.region
            )
        }
    }
}