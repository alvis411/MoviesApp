package com.quypham.assignment.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.quypham.assignment.R

object AppRoute {
    const val MOVIE = "Movies"
    const val PEOPLE = "People"
    const val TV = "TV"
}

data class AppTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: AppTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    AppTopLevelDestination(
        route = AppRoute.MOVIE,
        selectedIcon = Icons.Default.LocalMovies,
        unselectedIcon = Icons.Default.LocalMovies,
        iconTextId = R.string.tab_movie
    ),
    AppTopLevelDestination(
        route = AppRoute.PEOPLE,
        selectedIcon = Icons.Default.People,
        unselectedIcon = Icons.Default.People,
        iconTextId = R.string.tab_people
    ),
    AppTopLevelDestination(
        route = AppRoute.TV,
        selectedIcon = Icons.Outlined.Tv,
        unselectedIcon = Icons.Outlined.Tv,
        iconTextId = R.string.tab_tv
    ),
)
