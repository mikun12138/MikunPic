package me.mikun.mikunpic.view.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.mikun.mikunpic.component.PicCarousel

@Composable
fun PagePicPreview() {

    val context = LocalPlatformContext.current

    val imageReqs = remember(context) {
        List(10) {
            ImageRequest.Builder(context)
                .data(
                    "http://127.0.0.1:8081/random"
                )
                .crossfade(true)
                .memoryCacheKey(it.toString())
                .build()
        }
    }

    val painters = imageReqs.map {
        rememberAsyncImagePainter(it)
    }

    PicCarousel(painters)
}