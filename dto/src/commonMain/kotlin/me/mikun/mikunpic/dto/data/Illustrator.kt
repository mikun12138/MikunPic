package me.mikun.mikunpic.dto.data

import kotlinx.serialization.Serializable

@Serializable
data class Illustrator(
    val id: Int,
    val name: String
)