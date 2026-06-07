package me.mikun.mikunpic

import kotlinx.coroutines.runBlocking
import me.mikun.mikunpic.client.Client.searchIllustrator
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedLogicDesktopTest {
    @Test
    fun example() {
        runBlocking {
            searchIllustrator(
                5,
                "a",
            )
        }
    }
}
