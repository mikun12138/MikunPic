package me.mikun.mikunpic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client.httpClient
import me.mikun.mikunpic.ui.theme.AppTheme
import me.mikun.mikunpic.view.Home
import me.mikun.mikunpic.view.LocalNavController
import me.mikun.mikunpic.view.Login
import me.mikun.mikunpic.view.Manage
import me.mikun.mikunpic.view.Nav
import mikunpic.app.shared.generated.resources.Res
import mikunpic.app.shared.generated.resources.rua
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {},
) {

    var loaded by remember { mutableStateOf(false) }
    // TODO:: make a timeline&trigger
    var startFadeInTrigger by remember { mutableStateOf(false) }

    val splashAlpha = remember {
        Animatable(0f)
    }

    val mainPageAlpha = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        launch {
            splashAlpha.animateTo(
                1f,
                tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }
        delay(1000)

        snapshotFlow { loaded }
            .first { it }

        launch {
            mainPageAlpha.animateTo(
                1f,
                tween(
                    500,
                    easing = FastOutSlowInEasing
                )
            )
            startFadeInTrigger = true
        }
        launch {
            splashAlpha.animateTo(
                0f,
                tween(
                    500,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

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

    val navController = rememberNavController()

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    AppTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = mainPageAlpha.value
                    }
            ) {
                CompositionLocalProvider(LocalNavController provides navController) {
                    NavHost(
                        navController = LocalNavController.current,
                        startDestination = Nav.Home
                    ) {
                        composable<Nav.Home> {
                            Home(
                                {
                                    loaded = true
                                },
                                startFadeInTrigger
                            )
                        }
                        composable<Nav.Manage> {
                            LaunchedEffect(Unit) {
                                httpClient.get("/auth").let {
                                    if (it.status == HttpStatusCode.Unauthorized) {
                                        navController.navigate(Nav.Login) {
                                            popUpTo(Nav.Manage) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            }

                            Manage()
                        }

                        composable<Nav.Login> {
                            LaunchedEffect(Unit) {
                                httpClient.get("/auth").let {
                                    if (it.status != HttpStatusCode.Unauthorized) {
                                        navController.navigate(Nav.Manage) {
                                            popUpTo(Nav.Login) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            }

                            Login()
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = splashAlpha.value
                    }
            ) {
                SplashScreen(
                    loaded
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    loaded: Boolean,
) {
    val rotation = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            380f,
            animationSpec = keyframes {
                durationMillis = 400

                0f at 0

                380f at 400 using FastOutSlowInEasing
            }
        )

    }

    LaunchedEffect(loaded) {
        if (loaded) {
            rotation.animateTo(
                -40f,
                animationSpec = keyframes {
                    durationMillis = 400

                    380f at 0

                    (-40f) at 400 using FastOutSlowInEasing
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.rua),
            null,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = rotation.value
                }
        )
    }
}


