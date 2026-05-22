package me.mikun.mikunpic.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicCarousel(
    models: List<Any>
) {
    val carouselState = rememberCarouselState { models.size }

    var fadeIn by remember { mutableStateOf(true) }

    val blurRadius by animateDpAsState(
        targetValue = if (carouselState.isScrollInProgress || fadeIn) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 600),
    )

    val foregroundAlpha by animateFloatAsState(
        targetValue = if (carouselState.isScrollInProgress || fadeIn) 0f else 1f,
        animationSpec = tween(durationMillis = 400),
    )

    LaunchedEffect(Unit) {
        fadeIn = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalCenteredHeroCarousel(
            state = carouselState,
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius),
            itemSpacing = 8.dp,
        ) { i ->
            AsyncImage(
                model = models[i],
                modifier = Modifier
                    .fillMaxHeight()
                    .maskClip(MaterialTheme.shapes.extraLarge),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        AsyncImage(
            model = models[carouselState.currentItem],
            modifier = Modifier
                .padding(32.dp)
                .fillMaxHeight()
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = foregroundAlpha
                    val scale = 0.95f + (foregroundAlpha * 0.05f)
                    scaleX = scale
                    scaleY = scale
                }
                .clip(MaterialTheme.shapes.extraLarge),
            contentScale = ContentScale.Fit,
            contentDescription = "",
        )
    }
}
