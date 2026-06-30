package me.mikun.mikunpic.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.component.PicCarousel

@Composable
fun PagePicPreview(
    onReady: () -> Unit,
    readyPop: Boolean
) {
    val context = LocalPlatformContext.current

    val randomApi = "${LocalConfig.current.server}/random"

    val imageReqs = remember(context, randomApi) {
        List(10) {
            ImageRequest.Builder(context)
                .data(
                    randomApi
                )
                .crossfade(true)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCacheKey(it.toString())
                .build()
        }
    }

    val painters = imageReqs.mapIndexed { index, request ->
        rememberAsyncImagePainter(
            model = request,
            onSuccess = {
                if (index == 0) {
                    onReady()
                }
            },
            onError = {
                if (index == 0) {
                    onReady()
                }
            },
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        PicCarousel(
            painters = painters,
            readyPop = readyPop,
        )
    }
}
