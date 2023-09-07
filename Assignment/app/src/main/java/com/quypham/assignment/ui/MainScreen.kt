package com.quypham.assignment.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.quypham.assignment.R
import com.quypham.assignment.ui.home.DetailMovieUiState
import com.quypham.assignment.ui.home.HomeMovieUIState
import com.quypham.assignment.ui.home.MovieScreen
import com.quypham.assignment.ui.navigation.AppBottomNavigationBar
import com.quypham.assignment.ui.navigation.AppNavigationActions
import com.quypham.assignment.ui.navigation.AppNavigationRail
import com.quypham.assignment.ui.navigation.AppRoute
import com.quypham.assignment.ui.navigation.AppTopLevelDestination
import com.quypham.assignment.ui.navigation.ModalNavigationDrawerContent
import com.quypham.assignment.ui.navigation.PermanentNavigationDrawerContent
import com.quypham.assignment.ui.utils.AppContentType
import com.quypham.assignment.ui.utils.AppNavigationContentPosition
import com.quypham.assignment.ui.utils.AppNavigationType
import com.quypham.assignment.ui.utils.DevicePosture
import com.quypham.assignment.ui.utils.isBookPosture
import com.quypham.assignment.ui.utils.isSeparating
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    homeUIState: HomeMovieUIState,
    detailUiState: DetailMovieUiState,
    closeDetailScreen: () -> Unit = {},
    onQueryChange: (String) -> Unit,
    retryFetchDetailMovie : (Int) -> Unit,
    navigateToDetail: (Int, String, AppContentType) -> Unit = { _,_, _ -> },
)  {
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     */
    val navigationType: AppNavigationType
    val contentType: AppContentType

    /**
     * We are using display's folding features to map the device postures a fold is in.
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) ->
            DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) ->
            DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

        else -> DevicePosture.NormalPosture
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = AppNavigationType.BOTTOM_NAVIGATION
            contentType = AppContentType.SINGLE_PANE
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = AppNavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture != DevicePosture.NormalPosture) {
                AppContentType.DUAL_PANE
            } else {
                AppContentType.SINGLE_PANE
            }
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                AppNavigationType.NAVIGATION_RAIL
            } else {
                AppNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
            contentType = AppContentType.DUAL_PANE
        }
        else -> {
            navigationType = AppNavigationType.BOTTOM_NAVIGATION
            contentType = AppContentType.SINGLE_PANE
        }
    }

    /**
     * Content inside Navigation Rail/Drawer can also be positioned at top, bottom or center for
     * ergonomics and reachability depending upon the height of the device.
     */
    val navigationContentPosition = when (windowSize.heightSizeClass) {
        WindowHeightSizeClass.Compact -> {
            AppNavigationContentPosition.TOP
        }
        WindowHeightSizeClass.Medium,
        WindowHeightSizeClass.Expanded -> {
            AppNavigationContentPosition.CENTER
        }
        else -> {
            AppNavigationContentPosition.TOP
        }
    }
    
    AppNavigationWrapper(
        navigationType = navigationType,
        contentType = contentType,
        displayFeatures = displayFeatures,
        navigationContentPosition = navigationContentPosition,
        appHomeUIState = homeUIState,
        closeDetailScreen = closeDetailScreen,
        navigateToDetail = navigateToDetail,
        detailUiState = detailUiState,
        onQueryChange = onQueryChange,
        retryFetchDetailMovie = retryFetchDetailMovie
    )
}
@Composable
private fun AppNavigationWrapper(
    navigationType: AppNavigationType,
    contentType: AppContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: AppNavigationContentPosition,
    appHomeUIState: HomeMovieUIState,
    detailUiState: DetailMovieUiState,
    onQueryChange: (String) -> Unit,
    closeDetailScreen: () -> Unit,
    retryFetchDetailMovie : (Int) -> Unit,
    navigateToDetail: (Int, String, AppContentType) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: AppRoute.MOVIE

    if (navigationType == AppNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        // TODO check on custom width of PermanentNavigationDrawer: b/232495216
        PermanentNavigationDrawer(drawerContent = {
            PermanentNavigationDrawerContent(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigationActions::navigateTo,
            )
        }) {
            AppContent(
                navigationType = navigationType,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                appHomeUIState = appHomeUIState,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                detailUiState = detailUiState,
                onQueryChange = onQueryChange,
                retryFetchDetailMovie = retryFetchDetailMovie
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent(
                    selectedDestination = selectedDestination,
                    navigationContentPosition = navigationContentPosition,
                    navigateToTopLevelDestination = navigationActions::navigateTo,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            AppContent(
                navigationType = navigationType,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                appHomeUIState = appHomeUIState,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                detailUiState = detailUiState,
                onQueryChange = onQueryChange,
                retryFetchDetailMovie = retryFetchDetailMovie
            ) {
                scope.launch {
                    drawerState.open()
                }
            }
        }
    }
}

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    navigationType: AppNavigationType,
    contentType: AppContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: AppNavigationContentPosition,
    appHomeUIState: HomeMovieUIState,
    detailUiState: DetailMovieUiState,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (AppTopLevelDestination) -> Unit,
    closeDetailScreen: () -> Unit,
    onQueryChange: (String) -> Unit,
    retryFetchDetailMovie : (Int) -> Unit,
    navigateToDetail: (Int, String, AppContentType) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == AppNavigationType.NAVIGATION_RAIL) {
            AppNavigationRail(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            AppNavHost(
                navController = navController,
                contentType = contentType,
                displayFeatures = displayFeatures,
                appHomeUIState = appHomeUIState,
                navigationType = navigationType,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                modifier = Modifier.weight(1f),
                detailUiState = detailUiState,
                onQueryChange = onQueryChange,
                retryFetchDetailMovie = retryFetchDetailMovie
            )
            AnimatedVisibility(visible = navigationType == AppNavigationType.BOTTOM_NAVIGATION) {
                AppBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    contentType: AppContentType,
    displayFeatures: List<DisplayFeature>,
    appHomeUIState: HomeMovieUIState,
    detailUiState: DetailMovieUiState,
    navigationType: AppNavigationType,
    closeDetailScreen: () -> Unit,
    onQueryChange: (String) -> Unit,
    navigateToDetail: (Int, String, AppContentType) -> Unit,
    retryFetchDetailMovie : (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.MOVIE,
    ) {
        composable(AppRoute.MOVIE) {
            MovieScreen(
                contentType = contentType,
                homeMovieUIState = appHomeUIState,
                navigationType = navigationType,
                displayFeatures = displayFeatures,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                detailMovieUiState = detailUiState,
                onQueryChange = onQueryChange,
                retryFetchDetailMovie = retryFetchDetailMovie
            )
        }
        composable(AppRoute.TV) {
            EmptyComingSoon(modifier, R.string.tv_series_coming_soon)
        }
        composable(AppRoute.PEOPLE) {
            EmptyComingSoon(modifier, R.string.actor_coming_soon)
        }
    }
}
