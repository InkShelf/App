package com.github.inkshelf.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Library : Screen("library", Icons.Filled.Menu, "Library")
    object Discover : Screen("discover", Icons.Filled.Search, "Discover")
    object Downloads : Screen("downloads", Icons.Filled.KeyboardArrowDown, "Downloads")
    object Settings : Screen("settings", Icons.Filled.Settings, "Settings")
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(Screen.Library, Screen.Discover, Screen.Downloads, Screen.Settings)

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route) }
            )
        }
    }
}



@Composable
@Preview
fun App() {

    MaterialTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Library.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Library.route) { LibraryScreen() }
                composable(Screen.Discover.route) { DiscoverScreen() }
                composable(Screen.Downloads.route) { DownloadsScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Library") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Replace with your comic grid/list
            Text(
                text = "Library Screen Content",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Discover") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Replace with your search UI and discover content
            Text(
                text = "Discover Screen Content",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Downloads") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Replace with your downloads list UI
            Text(
                text = "Downloads Screen Content",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Example setting item
            Text("Settings Screen Content", style = MaterialTheme.typography.h1)
            Spacer(modifier = Modifier.height(16.dp))
            // Add more settings items here
            Text("Option 1")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Option 2")
        }
    }
}