package com.blessingsoftware.blessingplay.core.presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.blessingsoftware.blessingplay.core.presentation.ui.theme.BlessingPlayTheme
import com.blessingsoftware.blessingplay.home.presentation.HomeScreen
import com.blessingsoftware.blessingplay.playlist_songs.presentation.PlaylistSongsScreen
import com.blessingsoftware.blessingplay.playlist_songs.presentation.PlaylistSongsViewModel
import com.blessingsoftware.blessingplay.splash.presentation.SplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlessingPlayTheme {
                SetStatusBarColor()
                PermissionHandler { isPermissionGranted ->
                    if (isPermissionGranted) {
                        Navigation()
                    }
                }
            }
        }
    }

    @Composable
    fun Navigation(modifier: Modifier = Modifier) {
        val navController = rememberNavController()

        NavHost(
            modifier = modifier
                .fillMaxSize()
                .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
                .background(Color.Transparent),
            navController = navController,
            startDestination = Screen.Splash
        ) {
            composable<Screen.Splash> {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Splash) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<Screen.Home> {
                HomeScreen(navController)
            }

            composable<Screen.PlaylistSongsScreen> {
                val args = it.toRoute<Screen.PlaylistSongsScreen>()
                PlaylistSongsScreen(
                    navController = navController,
                    playlistId = args.playlistId,
                    name = args.name
                )
            }
        }
    }
}

@Composable
private fun SetStatusBarColor() {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (isDarkTheme) Color.Black else Color.White,
            darkIcons = !isDarkTheme
        )
    }
}

@Composable
private fun PermissionHandler(onPermissionResult: @Composable (Boolean) -> Unit) {
    val context = LocalContext.current
    var isPermissionGranted by remember { mutableStateOf(false) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        isPermissionGranted = results.values.all { it }
        if (!isPermissionGranted) {
            Toast.makeText(
                context,
                "Ứng dụng cần quyền để truy cập bộ nhớ!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(permissions)
    }
    if (isPermissionGranted) {
        onPermissionResult(true)
    }
}






