package me.mikun.mikunpic

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform