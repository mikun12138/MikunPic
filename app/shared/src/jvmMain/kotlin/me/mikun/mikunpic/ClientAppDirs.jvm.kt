package me.mikun.mikunpic

import net.harawata.appdirs.AppDirsFactory

actual object ClientAppDirs {
    actual val config: String
        get() = AppDirsFactory.getInstance()
            .getUserConfigDir(APP_NAME, null, null)

}