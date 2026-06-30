package me.mikun.mikunpic.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.mikun.mikunpic.shared.generated.resources.Res
import me.mikun.mikunpic.shared.generated.resources.rua
import org.jetbrains.compose.resources.painterResource

enum class SplashState {
    Start,
    LogoIn,
    End,
}

@Composable
fun SplashScreen(
    splashState: MutableTransitionState<SplashState>,
) {
    val splashAlpha = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        splashState.targetState = SplashState.LogoIn
        launch {
            splashAlpha.animateTo(
                1f,
                tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing,
                ),
            )
        }

        delay(1000)

        snapshotFlow { splashState.currentState == SplashState.End }
            .first { it }

        launch {
            splashAlpha.animateTo(
                0f,
                tween(
                    500,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
    }

    val transition =
        rememberTransition(
            transitionState = splashState,
        )

    val rotation by transition.animateFloat(
        transitionSpec = {
            when {
                SplashState.Start isTransitioningTo SplashState.LogoIn -> {
                    tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing,
                    )
                }

                SplashState.LogoIn isTransitioningTo SplashState.End -> {
                    tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing,
                    )
                }

                else -> {
                    tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing,
                    )
                }
            }
        },
    ) { phase ->
        when (phase) {
            SplashState.Start -> 0f
            SplashState.LogoIn -> 380f
            SplashState.End -> -40f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                this.alpha = splashAlpha.value
            }
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.rua),
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            },
        )
    }
}
