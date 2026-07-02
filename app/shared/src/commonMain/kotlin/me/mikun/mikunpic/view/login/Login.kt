package me.mikun.mikunpic.view.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.mikun.mikunpic.LocalPref
import me.mikun.mikunpic.view.LocalNavController
import me.mikun.mikunpic.view.Nav

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun Login() {
    val textField = rememberTextFieldState()

    val navController = LocalNavController.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(textField)

            OutlinedButton(
                onClick = {
                    LocalPref = LocalPref.copy(
                        token = textField.text.toString()
                    )

                    navController.navigate(Nav.Manage) {
                        launchSingleTop = true
                        popUpTo(Nav.Login) {
                            inclusive = true
                        }
                    }

                },
            ) {
                Text("Login")
            }
        }
    }
}
