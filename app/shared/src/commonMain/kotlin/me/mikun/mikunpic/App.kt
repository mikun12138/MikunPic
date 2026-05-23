package me.mikun.mikunpic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import me.mikun.mikunpic.client.httpClient
import me.mikun.mikunpic.view.Home


@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        SingletonImageLoader.setSafe {
            ImageLoader.Builder(it)
                .components {
                    add(
                        KtorNetworkFetcherFactory(
                            httpClient
                        )
                    )
                }
                .build()
        }
    }

    Home()

}
