package me.mikun.mikunpic.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.PicCarousel(
    painters: List<Painter>,
    readyPop: Boolean,
) {
    val actualSize = painters.size

    val carouselState = rememberCarouselState(
        initialItem =
            114514 / 2,
    ) {
        114514
    }

    val bgBlurRadius by animateDpAsState(
        targetValue = if (
            carouselState.isScrollInProgress || !readyPop
        ) {
            0.dp
        } else {
            20.dp
        },

        animationSpec = tween(600),
    )

    val fgAlpha by animateFloatAsState(
        targetValue = if (
            carouselState.isScrollInProgress || !readyPop
        ) {
            0f
        } else {
            1f
        },

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    HorizontalCenteredHeroCarousel(
        state = carouselState,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) { index ->
        val realIndex =
            index % actualSize

        fun shouldLoad(index: Int): Boolean = abs(index - carouselState.currentItem) <= 2
        if (shouldLoad(index)) {
            val painter = painters[realIndex]
            val imageShape = MaterialTheme.shapes.extraLarge
            val painterSize = painter.intrinsicSize
            val painterAspectRatio =
                if (
                    painterSize.width.isFinite() &&
                    painterSize.height.isFinite() &&
                    painterSize.width > 0f &&
                    painterSize.height > 0f
                ) {
                    painterSize.width / painterSize.height
                } else {
                    null
                }

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .maskClip(imageShape)
                    .blur(bgBlurRadius),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
            )

            if (index == carouselState.currentItem) {
                BoxWithConstraints(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center,
                ) {
                    val availableAspectRatio =
                        if (maxHeight.value > 0f) {
                            maxWidth.value / maxHeight.value
                        } else {
                            0f
                        }

                    val foregroundModifier =
                        if (painterAspectRatio != null && availableAspectRatio > 0f) {
                            if (painterAspectRatio > availableAspectRatio) {
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(painterAspectRatio)
                            } else {
                                Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(painterAspectRatio)
                            }
                        } else {
                            Modifier.fillMaxHeight()
                        }

                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = foregroundModifier
                            .graphicsLayer {
                                alpha = fgAlpha
                                shape = imageShape
                                clip = true

                                val scale =
                                    0.95f + (fgAlpha * 0.05f)

                                scaleX = scale
                                scaleY = scale
                            },
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}
