package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

actual object ClientAppDirs {
    actual val config: String
        @Composable get() = File(LocalContext.current.filesDir, APP_NAME).absolutePath
}
