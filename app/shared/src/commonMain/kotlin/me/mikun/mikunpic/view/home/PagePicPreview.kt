package me.mikun.mikunpic.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.mikun.mikunpic.LocalConfig
import me.mikun.mikunpic.component.PicCarousel
import me.mikun.mikunpic.component.act.PicCardPopup

@Composable
fun PagePicPreview(
    onReady: () -> Unit,
    readyPop: Boolean,
) {
    var showPicCardPopup by remember { mutableStateOf(false) }
    var picUrlToPopup by remember { mutableStateOf<String?>(null) }
    var picToPopupMemoryCacheKey by remember { mutableStateOf<String?>(null) }
    if (showPicCardPopup && picUrlToPopup != null) {
        PicCardPopup(
            show = showPicCardPopup,
            onDismissRequest = {
                showPicCardPopup = false
                picUrlToPopup = null
            },
            picUrl = picUrlToPopup!!,
            memoryCacheKey = picToPopupMemoryCacheKey,
            diskCacheKey = null,
        )
    }

    val localPlatformContext = LocalPlatformContext.current

    val previewApi = "${LocalConfig.current.server}${LocalConfig.current.previewApi}"

    val imageReqs = remember(localPlatformContext, previewApi) {
        List(10) {
            ImageRequest.Builder(localPlatformContext)
                .data(
                    previewApi,
                )
                .crossfade(true)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCacheKey("$previewApi$it")
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
            onClick = { index ->
                showPicCardPopup = true
                picUrlToPopup = previewApi
                picToPopupMemoryCacheKey = "$previewApi$index"
            },
            readyPop = readyPop,
        )
    }
}
