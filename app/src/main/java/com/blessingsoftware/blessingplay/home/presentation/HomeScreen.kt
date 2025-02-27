package com.blessingsoftware.blessingplay.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.blessingplay.home.screens.song_list.presentation.SongListScreen
import com.blessingsoftware.blessingplay.home.screens.playlist.presentation.PlayListScreen
import com.blessingsoftware.blessingplay.home.screens.setting.presentation.SettingScreen

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val bottomNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Song list",
            route = "song_list",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu
        ),
        BottomNavItem(
            title = "Playlist",
            route = "playlist",
            selectedIcon = Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Outlined.PlayArrow
        ),
        BottomNavItem(
            title = "Setting",
            route = "setting",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(bottomNavController, bottomNavItems)
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "song_list",
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            composable("song_list") {
                SongListScreen()
            }
            composable("playlist") {
                PlayListScreen()
            }
            composable("setting") {
                SettingScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    bottomNavController: NavController,
    bottomNavItems: List<BottomNavItem>
) {
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = Modifier.height(65.dp)) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = false,
                onClick = {
                    bottomNavController.navigate(item.route) {
                        popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        color = if (currentRoute == item.route) Color.Green else Color.White
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color.Green else Color.White,
                    )
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
