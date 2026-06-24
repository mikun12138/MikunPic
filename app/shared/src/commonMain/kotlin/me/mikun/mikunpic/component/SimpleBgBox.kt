package me.mikun.mikunpic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size

@Composable
fun SimpleBgBox(
    model: Any,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AsyncImage(
            ImageRequest.Builder(LocalPlatformContext.current)
                .data(model)
                .diskCacheKey(model.toString())
                .memoryCacheKey(model.toString())
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build(),
            null,
            modifier = Modifier
                .fillMaxSize()
                .blur(0.5f.dp),
            contentScale = ContentScale.Crop,
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
        ) {
            content()
        }
    }
}
