package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ListItemDefaults.verticalAlignment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Pic

@Composable
@Preview(showBackground = true)
fun Manage() {

    val pages = listOf<@Composable () -> Unit>(
        {
            UploadPic()
        },
        {
            EditTable()
        }
    )

    VerticalPager(
        state = rememberPagerState { pages.size },
        modifier = Modifier
            .fillMaxSize()
    ) {
        pages[it]()
    }
}

@Composable
private fun UploadPic() {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ElevatedCard(
            modifier = Modifier.align(Alignment.Center)
        ) {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        FileKit.openFilePicker()?.let { file ->
                            Client.uploadPic(
                                picName = file.name,
                                picBytes = file.readBytes()
                            )
                        }
                    }
                }
            ) {
                Text("Upload")
            }
        }
    }
}

@Composable
private fun EditTable() {

    val coroutineContext = rememberCoroutineScope()

    var picOnTable by remember { mutableStateOf<Pic?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElevatedButton(
                    onClick = {
                        coroutineContext.launch {
                            picOnTable = Client.randomPic(
                                1,
                            ).pics.firstOrNull()
                        }
                    }
                ) {
                    Text("No author")
                }

                ElevatedButton(
                    onClick = {
                        coroutineContext.launch {
                            picOnTable = Client.randomPic(1).pics.firstOrNull()
                        }
                    }
                ) {
                    Text("No tag")
                }
            }

            if (picOnTable != null) {
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                ) {
                    AsyncImage(
                        "${Client.baseUrl}/pic/${picOnTable?.filename}",
                        contentDescription = null
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text("illustrator: ")

                        ElevatedAssistChip(
                            onClick = { },
                            label = {
                                picOnTable?.illustrator?.let {
                                    Text(it)
                                }
                            }
                        )
                    }
                }
            }


        }
    }
}