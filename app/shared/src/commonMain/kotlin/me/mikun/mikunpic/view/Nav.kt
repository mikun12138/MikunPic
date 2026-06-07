package me.mikun.mikunpic.view

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

interface Nav {
    @Serializable
    object Home : Nav

    @Serializable
    object Login : Nav

    @Serializable
    object Manage : Nav
}

val LocalNavController =
    compositionLocalOf<NavHostController> {
        error("LocalNavController not provided")
    }
