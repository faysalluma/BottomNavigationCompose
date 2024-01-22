package com.groupec.bottombarnavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.groupec.bottombarnavigation.navigation.BottomNavigationItem
import com.groupec.bottombarnavigation.ui.theme.BottomBarNavigationTheme
import com.groupec.bottombarnavigation.screens.Home
import com.groupec.bottombarnavigation.screens.Search
import com.groupec.bottombarnavigation.screens.Settings

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxWidth(),
                    bottomBar = {
                        BottomNavBar(
                            navHostController = navHostController,
                            items = BottomNavigationItem.getMenuBottomItems()
                        )
                    },
                    content = {
                        NavHost(
                            navController = navHostController,
                            startDestination = BottomNavigationItem.getMenuBottomItems()[0].route,
                            modifier = Modifier
                                .padding(it)
                        ) {
                            composable(BottomNavigationItem.getMenuBottomItems()[0].route) { Home() }
                            composable(BottomNavigationItem.getMenuBottomItems()[1].route) { Search() }
                            composable(BottomNavigationItem.getMenuBottomItems()[2].route) { Settings() }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun BottomNavBar(
    navHostController: NavHostController,
    items: List<BottomNavigationItem>
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEachIndexed { index, bottomNavigationItem ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == bottomNavigationItem.route } == true,
                // onClick = { navHostController.navigate(bottomNavigationItem.route) },
                onClick = {
                    selectedIndex = index
                    navHostController.navigate(bottomNavigationItem.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navHostController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (index == selectedIndex) {
                            bottomNavigationItem.selectedIcon
                        } else bottomNavigationItem.unselectedIcon,
                        contentDescription = bottomNavigationItem.title
                    )
                },
                label = { Text(text = bottomNavigationItem.title) },
                alwaysShowLabel = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BottomBarNavigationTheme {
        Greeting("Android")
    }
}