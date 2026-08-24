package me.mikun.mikunpic.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.mikun.mikunpic.LocalPref
import me.mikun.mikunpic.component.SimpleBgBox
import me.mikun.mikunpic.component.button.OutlinedFloatingActionButton
import me.mikun.mikunpic.component.card.AcrylicCard
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
            val navController = LocalNavController.current
            Scaffold(
                floatingActionButton = {
                    OutlinedFloatingActionButton(
                        onClick = {
                            navController.navigate(Nav.Manage)
                        }
                    ) {
                        Icon(
                            Icons.Default.ViewTimeline,
                            contentDescription = null
                        )
                    }
                }
            ) {

                SimpleBgBox(
                    LocalPref.bg.home.api,
                ) {
                    PageApi()
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
