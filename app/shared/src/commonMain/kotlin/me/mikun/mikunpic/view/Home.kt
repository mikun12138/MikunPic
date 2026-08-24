package me.mikun.mikunpic.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.mikun.mikunpic.LocalPref
import me.mikun.mikunpic.component.SimpleBgBox
import me.mikun.mikunpic.view.home.PageApi
import me.mikun.mikunpic.view.home.PagePicPreview

@Composable
fun Home(
    onReady: () -> Unit,
    readyPop: Boolean,
) {
    val pages = listOf<@Composable () -> Unit>(
        {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                PagePicPreview(
                    onReady,
                    readyPop,
                )
            }
        },
        {
            SimpleBgBox(
                LocalPref.bg.home.api,
            ) {
                PageApi()
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
