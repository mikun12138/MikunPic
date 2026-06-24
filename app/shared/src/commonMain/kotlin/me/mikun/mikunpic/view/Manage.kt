package me.mikun.mikunpic.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.component.SimpleBgBox
import me.mikun.mikunpic.view.manage.EditTable
import me.mikun.mikunpic.view.manage.ManageOverview

@Composable
fun Manage() {
    val pages = listOf<@Composable () -> Unit>(
        {
            SimpleBgBox(
                LocalConfig.bg.manage.upload,
            ) {
                ManageOverview()
            }
        },
        {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                EditTable()
            }
        },
    )

    VerticalPager(
        state = rememberPagerState { pages.size },
        modifier = Modifier
            .fillMaxSize(),
    ) {
        pages[it]()
    }
}
