package com.robstore.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.robstore.features.authentication.login.di.AppModule as LoginAppModule
import com.robstore.features.authentication.recoveryPassword.di.AppModule as RecoveryAppModule
import com.robstore.features.authentication.login.presentation.view.LoginScreen
import com.robstore.features.authentication.recoveryPassword.presentation.view.RecoveryPasswd
import com.robstore.features.authentication.register.presentation.view.RegisterScreen
import com.robstore.features.authentication.login.presentation.viewModel.LoginViewModel
import com.robstore.features.authentication.login.presentation.viewModel.LoginViewModelFactory
import com.robstore.features.authentication.recoveryPassword.presentation.viewModel.RecoveryPasswdViewModel
import com.robstore.features.home.presentation.view.Home

import com.robstore.features.authentication.recoveryPassword.presentation.viewModel.RecoveryViewModelFactory
import com.robstore.features.authentication.register.di.RegisterAppModule
import com.robstore.features.authentication.register.presentation.viewModel.RegisterViewModel
import com.robstore.features.authentication.register.presentation.viewModel.RegisterViewModelFactory
import com.robstore.features.home.di.HomeAppModul
import com.robstore.features.home.presentation.viewModel.HomeViewModel
import com.robstore.features.home.presentation.viewModel.HomeViewModelFactory
import com.robstore.features.myApps.di.MyAppsModule
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModel
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModelFactory
import com.robstore.features.profile.di.UpdateUserAppModule
import com.robstore.features.profile.presentation.viewModel.ProfileViewModel
import com.robstore.features.profile.presentation.viewModel.ProfileViewModelFactory


@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    val factory = remember {
        LoginViewModelFactory(
            LoginAppModule.loginUseCase,
            LoginAppModule.getDataStoreManager(),
            LoginAppModule.getInternetConnectivityUseCase()
        )
    }
    val loginViewModel: LoginViewModel = viewModel(factory = factory)

    val initialDestination by loginViewModel.initialDestination.collectAsState()

    val registerViewModelFactory = remember {
        RegisterViewModelFactory(
            RegisterAppModule.registerUseCase,
            LoginAppModule.getDataStoreManager()
        )
    }
    val registerViewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)

    val recoveryViewModelFactory = remember { RecoveryViewModelFactory(RecoveryAppModule.recoveryUseCase) }
    val recoveryPasswdViewModel: RecoveryPasswdViewModel = viewModel(factory = recoveryViewModelFactory)



    val homeViewModelFactory = remember {
        HomeViewModelFactory(
            LoginAppModule.getLocationUseCase(),
            LoginAppModule.getDataStoreManager(),
            homeUseCase = HomeAppModul.HomeAppModule.getHomeUseCase()
        )
    }
    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
    val context = LocalContext.current

    val myAppsViewModelFactory = remember {
        MyAppsViewModelFactory(
            MyAppsModule.myAppsUseCase,
            applicationContext = context,
            MyAppsModule.myAppsNotificationsUseCase,
        )
    }
    val myAppsViewModel: MyAppsViewModel = viewModel(factory = myAppsViewModelFactory)


    val profileViewModelFactory = remember { ProfileViewModelFactory(
        UpdateUserAppModule.updateUserUseCase,
        LoginAppModule.getDataStoreManager(),
        LoginAppModule.getUserRepository(),
        LoginAppModule.getInternetConnectivityUseCase()

    )}
    val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)

    LaunchedEffect(initialDestination) {
        if (initialDestination != null) {
            if (navController.currentDestination?.route != initialDestination) {
                navController.navigate(initialDestination!!) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }


    if (initialDestination != null) {
        NavHost(
            navController = navController,
            startDestination = when (initialDestination) {
                "home" -> NavigationRoutes.HOME
                "login" -> NavigationRoutes.LOGIN
                else -> NavigationRoutes.LOGIN
            }
        ) {
            composable(NavigationRoutes.LOGIN) {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    onNavigateToRegister = { navController.navigate(NavigationRoutes.REGISTER) },
                    onNavigateToRecoveryPasswd = { navController.navigate(NavigationRoutes.RecPasswd) },
                    onNavigateToHome = {
                        navController.navigate(NavigationRoutes.HOME) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }

            composable(NavigationRoutes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    registerViewModel = registerViewModel,
                    onNavigateToHome = {navController.navigate(NavigationRoutes.HOME)},
                )
            }
            composable(NavigationRoutes.RecPasswd) {
                RecoveryPasswd(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    recoveryPasswdViewModel = recoveryPasswdViewModel,
                )
            }
            composable(NavigationRoutes.HOME) {
                Home(
                    onNavigateToLogin = {
                        navController.navigate(NavigationRoutes.LOGIN) {
                            popUpTo(NavigationRoutes.HOME) { inclusive = true }
                        }
                    },
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    myAppsViewModel = myAppsViewModel,
                )
            }
        }
    }


}
