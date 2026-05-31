package me.mikun.mikunpic.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.mikun.mikunpic.view.home.PageApi
import me.mikun.mikunpic.view.home.PagePicPreview
import me.mikun.mikunpic.view.login.Login
import me.mikun.mikunpic.view.manage.Manage

@Composable
fun Home(
    onReady: () -> Unit,
    startFadeInTrigger: Boolean,
) {

    MaterialTheme {
        val pages = listOf<@Composable () -> Unit>(
            {
                PagePicPreview(
                    onReady,
                    startFadeInTrigger
                )
            },
            {
                PageApi()
            },
        )

        VerticalPager(
            state = rememberPagerState { pages.size },
            modifier = Modifier
                .fillMaxSize()
        ) {
            pages[it]()
        }
    }
}

