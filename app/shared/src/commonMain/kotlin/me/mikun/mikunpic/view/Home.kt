package me.mikun.mikunpic.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.component.SimpleBgBox
import me.mikun.mikunpic.view.home.PageApi
import me.mikun.mikunpic.view.home.PagePicPreview

@Composable
fun Home(
    onReady: () -> Unit,
    startFadeInTrigger: Boolean,
) {
    val pages = listOf<@Composable () -> Unit>(
        {
            PagePicPreview(
                onReady,
                startFadeInTrigger,
            )
        },
        {
            SimpleBgBox(
                LocalConfig.bg.home.api,
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
