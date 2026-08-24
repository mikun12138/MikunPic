package me.mikun.mikunpic.component.act

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size

@Composable
fun PicCardPopup(
    show: Boolean,
    onDismissRequest: () -> Unit,
    picUrl: String,
    memoryCacheKey: String? = picUrl,
    diskCacheKey: String? = picUrl,
) {
    val localPlatformContext = LocalPlatformContext.current
    var imageAspectRatio by remember(picUrl) { mutableStateOf<Float?>(null) }

    if (show) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismissRequest,
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    val aspectRatio = imageAspectRatio ?: 1f
                    val maxAspectRatio = maxWidth.value / maxHeight.value
                    val imageModifier = if (aspectRatio > maxAspectRatio) {
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio)
                    } else {
                        Modifier
                            .fillMaxHeight()
                            .aspectRatio(aspectRatio)
                    }

                    Box(
                        modifier = imageModifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {},
                            ),
                    ) {
                        AsyncImage(
                            ImageRequest.Builder(localPlatformContext)
                                .data(picUrl)
                                .size(Size.ORIGINAL)
                                .apply {
                                    memoryCacheKey?.let {
                                        memoryCacheKey(it)
                                    } ?: memoryCachePolicy(CachePolicy.DISABLED)
                                }.apply {
                                    diskCacheKey?.let {
                                        diskCacheKey(it)
                                    } ?: diskCachePolicy(CachePolicy.DISABLED)
                                }
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            onSuccess = {
                                val intrinsicSize = it.painter.intrinsicSize
                                if (intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
                                    imageAspectRatio = intrinsicSize.width / intrinsicSize.height
                                }
                            },
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}
