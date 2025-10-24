package com.example.buttons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.buttons.ui.*
import com.example.buttons.ui.theme.ButtonsTheme
import com.example.buttons.viewmodel.ButtonViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ButtonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ButtonsTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ButtonsApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun ButtonsApp(viewModel: ButtonViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAbout = { navController.navigate("about") },
                onEditButton = { button ->
                    if (button != null) {
                        navController.navigate("editButton/${button.id}")
                    } else {
                        navController.navigate("editButton/-1")
                    }
                }
            )
        }

        composable(
            "editButton/{buttonId}",
            arguments = listOf(navArgument("buttonId") { type = NavType.LongType })
        ) { backStackEntry ->
            val buttonId = backStackEntry.arguments?.getLong("buttonId")
            EditButtonScreen(
                viewModel = viewModel,
                buttonId = if (buttonId == -1L) null else buttonId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("about") {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
