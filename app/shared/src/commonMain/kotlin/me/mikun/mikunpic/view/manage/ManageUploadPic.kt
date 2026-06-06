package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client

@Composable
fun UploadPic() {
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