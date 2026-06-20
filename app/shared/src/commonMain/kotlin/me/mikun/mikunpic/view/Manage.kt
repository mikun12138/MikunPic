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
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.view.manage.EditTable
import me.mikun.mikunpic.view.manage.ManageOverview

@Composable
fun Manage() {
    val pages = listOf<@Composable () -> Unit>(
        {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                AsyncImage(
                    LocalConfig.bg.manage.upload,
                    null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    ManageOverview()
                }
            }
        },
        {
            Box(
                Modifier.fillMaxSize()
            ) {

                AsyncImage(
                    LocalConfig.bg.manage.editTable,
                    null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))

                ) {
                    EditTable()
                }
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
