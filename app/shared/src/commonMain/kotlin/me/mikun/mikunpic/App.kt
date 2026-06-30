package me.mikun.mikunpic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.client.Client.httpClient
import me.mikun.mikunpic.component.SplashScreen
import me.mikun.mikunpic.component.SplashState
import me.mikun.mikunpic.ui.theme.AppTheme
import me.mikun.mikunpic.view.Home
import me.mikun.mikunpic.view.LocalNavController
import me.mikun.mikunpic.view.login.Login
import me.mikun.mikunpic.view.Manage
import me.mikun.mikunpic.view.Nav

@Composable
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {},
) {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalConfig provides LoadConfig(),
        LocalNavController provides navController
    ) {
        Client.Init()

        LaunchedEffect(navController) {
            onNavHostReady(navController)
        }

        AppInternal()
    }
}

@Composable
private fun AppInternal() {
    val splashState = remember {
        MutableTransitionState(SplashState.Start)
    }

    val readyPop by remember {
        derivedStateOf {
            splashState.currentState == SplashState.End
        }
    }

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            val localNavController = LocalNavController.current
            NavHost(
                navController = LocalNavController.current,
                startDestination = Nav.Home,
            ) {
                composable<Nav.Home> {
                    Home(
                        {
                            splashState.targetState = SplashState.End
                        },
                        readyPop
                    )
                }
                composable<Nav.Manage> {
                    LaunchedEffect(Unit) {
                        httpClient.get("/auth").let {
                            if (it.status != HttpStatusCode.OK) {
                                localNavController.navigate(Nav.Login) {
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
                            if (it.status == HttpStatusCode.OK) {
                                localNavController.navigate(Nav.Manage) {
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

        SplashScreen(
            splashState,
        )
    }
}

