package com.blessingsoftware.blessingplay.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blessingsoftware.blessingplay.R
import com.blessingsoftware.blessingplay.home.screens.more_song.presentation.MoreSongScreen
import com.blessingsoftware.blessingplay.home.screens.music_player.presentation.MusicPlayerScreen
import com.blessingsoftware.blessingplay.home.screens.song_list.presentation.SongListScreen
import com.blessingsoftware.blessingplay.home.screens.playlist.presentation.PlaylistScreen
import com.blessingsoftware.blessingplay.home.screens.setting.presentation.SettingScreen

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val bottomNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Song list",
            screen = BottomNavScreen.SongList,
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu
        ),
        BottomNavItem(
            title = "Playlist",
            screen = BottomNavScreen.Playlist,
            selectedIcon = Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Outlined.PlayArrow
        ),
        BottomNavItem(
            title = "",
            screen = BottomNavScreen.MusicPlayer,
            selectedIcon = null,
            unselectedIcon = null
        ),
        BottomNavItem(
            title = "More song",
            screen = BottomNavScreen.MoreSong,
            selectedIcon = Icons.Filled.Download,
            unselectedIcon = Icons.Outlined.Download
        ),
        BottomNavItem(
            title = "Setting",
            screen = BottomNavScreen.Setting,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),
    )

    Box {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(bottomNavController, bottomNavItems)
            },
            content = { paddingValues ->
                NavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavScreen.MusicPlayer,
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    composable<BottomNavScreen.SongList> {
                        SongListScreen(bottomNavController = bottomNavController)
                    }
                    composable<BottomNavScreen.Playlist> {
                        PlaylistScreen(
                            navController = navController,
                            bottomNavController = bottomNavController
                        )
                    }
                    composable<BottomNavScreen.MusicPlayer> { MusicPlayerScreen() }
                    composable<BottomNavScreen.MoreSong> { MoreSongScreen() }
                    composable<BottomNavScreen.Setting> { SettingScreen() }
                }
            }
        )
    }
}

@Composable
fun BottomNavigationBar(
    bottomNavController: NavController,
    bottomNavItems: List<BottomNavItem>,
) {
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringAfterLast(".")

    Column {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.DarkGray.copy(alpha = 0.5f)
        )

        NavigationBar(
            containerColor = Color.Black,
            tonalElevation = 15.dp
        ) {
            bottomNavItems.forEach { item ->
                if (item.screen == BottomNavScreen.MusicPlayer) {
                    Image(
                        painter = painterResource(id = R.drawable.vinyl_bg),
                        contentDescription = "Music player button",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .size(62.dp)
                            .clip(CircleShape)
                            .clickable {
                                bottomNavController.navigate(item.screen) {
                                    popUpTo(bottomNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                    )
                } else {
                    NavigationBarItem(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = false,
                        onClick = {
                            bottomNavController.navigate(item.screen) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                color = if (currentRoute == item.screen.toString()) Color.Green else Color.White
                            )
                        },
                        icon = {
                            (if (currentRoute == item.screen.toString()) item.selectedIcon else item.unselectedIcon)?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = item.title,
                                    tint = if (currentRoute == item.screen.toString()) Color.Green else Color.White,
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val screen: BottomNavScreen,
    val selectedIcon: ImageVector?,
    val unselectedIcon: ImageVector?
)
