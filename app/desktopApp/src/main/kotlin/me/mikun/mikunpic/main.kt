package me.mikun.mikunpic

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MikunPic",
    ) {
        App()
    }
}