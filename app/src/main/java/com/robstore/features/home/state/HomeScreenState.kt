package com.robstore.features.home.state

sealed class HomeScreen {
    object AppList : HomeScreen()
    object SearchApp : HomeScreen()
    object MyApps : HomeScreen()
    object Profile : HomeScreen()

}