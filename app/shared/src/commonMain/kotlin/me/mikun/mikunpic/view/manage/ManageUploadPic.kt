package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import me.mikun.mikunpic.Config
import me.mikun.mikunpic.client.Client

@Composable
fun UploadPic() {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AsyncImage(
            Config.Bg.Manage.Upload,
            null,
            modifier = Modifier.fillMaxSize()
            ,
            contentScale = ContentScale.Crop
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        ElevatedCard(
            modifier = Modifier.align(Alignment.Center),
        ) {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        FileKit.openFilePicker()?.let { file ->
                            Client.uploadPic(
                                picName = file.name,
                                picBytes = file.readBytes(),
                            )
                        }
                    }
                },
            ) {
                Text("Upload")
            }
        }
    }
}
