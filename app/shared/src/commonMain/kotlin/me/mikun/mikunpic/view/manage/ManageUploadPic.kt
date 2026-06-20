package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client

@Composable
fun BoxScope.ManageOverview() {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.align(Alignment.Center),
    ) {
        ElevatedCard {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        Client.sync()
                    }
                },
            ) {
                Text("Sync")
            }
        }

        ElevatedCard {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        FileKit.openDirectoryPicker()?.let { dir ->
                            dir.list().forEach {
                                Client.uploadPic(
                                    it.name,
                                    it.readBytes()
                                )
                            }
                        }
                    }
                },
            ) {
                Text("Upload")
            }
        }
    }
}
