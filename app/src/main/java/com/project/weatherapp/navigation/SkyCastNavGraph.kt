package com.project.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.weatherapp.ui.screens.auth.SignInScreen
import com.project.weatherapp.ui.screens.auth.SignUpScreen
import com.project.weatherapp.ui.screens.main.MainScreen

@Composable
fun SkyCastNavGraph() {
    val navController = rememberNavController()
    val auth = remember { FirebaseAuth.getInstance() }

    // If user is already logged in, start directly on main screen
    val startDestination = if (auth.currentUser != null) "main" else "signin"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("signin") {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }
        
        composable("signup") {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            MainScreen(
                onLogout = {
                    auth.signOut()
                    navController.navigate("signin") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
