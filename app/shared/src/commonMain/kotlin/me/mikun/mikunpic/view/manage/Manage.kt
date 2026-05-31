package me.mikun.mikunpic.view.manage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.engine.ProxyBuilder.http
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.Input
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.source
import me.mikun.mikunpic.client.httpClient
import me.mikun.mikunpic.view.LocalNavController
import me.mikun.mikunpic.view.Nav

@Composable
@Preview(showBackground = true)
fun Manage() {
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
                            val byteArray = file.readBytes()
                            httpClient.submitFormWithBinaryData(
                                url = "http://127.0.0.1:8080/manage/upload",
                                formData = formData {
                                    appendInput(
                                        "file",
                                        Headers.build {
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${file.name}\""
                                            )
                                        }
                                    ) {
                                        Buffer().apply {
                                            write(byteArray)
                                        }
                                    }
                                }
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