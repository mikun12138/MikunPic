package me.mikun.mikunpic.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Label
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mikun.mikunpic.LocalPref
import me.mikun.mikunpic.component.SimpleBgBox
import me.mikun.mikunpic.view.manage.ManageEditTable
import me.mikun.mikunpic.view.manage.ManageOverview
import me.mikun.mikunpic.view.manage.ManageStorages
import me.mikun.mikunpic.viewmodel.ManageViewModel

@Composable
fun Manage(
    viewModel: ManageViewModel = viewModel { ManageViewModel() }
) {
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

    Box {
        VerticalPager(
            state = rememberPagerState { pages.size },
            modifier = Modifier
                .fillMaxSize(),
        ) {
            pages[it]()
        }
    }
}
