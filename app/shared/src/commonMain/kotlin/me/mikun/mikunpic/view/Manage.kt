package me.mikun.mikunpic.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.mikun.mikunpic.LocalPref
import me.mikun.mikunpic.component.SimpleBgBox
import me.mikun.mikunpic.view.manage.ManageEditTable
import me.mikun.mikunpic.view.manage.ManageOverview
import me.mikun.mikunpic.view.manage.ManageStorages

@Composable
fun Manage() {
    val pages = listOf<@Composable () -> Unit>(
        {
            SimpleBgBox(
                LocalPref.bg.manage.upload,
            ) {
                ManageOverview()
            }
        },
        {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                ManageEditTable()
            }
        },
        {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                ManageStorages()
            }
        }
    )

    VerticalPager(
        state = rememberPagerState { pages.size },
        modifier = Modifier
            .fillMaxSize(),
    ) {
        pages[it]()
    }
}
