package com.robstore.features.home.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.robstore.R
import com.robstore.core.hardware.camera.data.repository.CameraManager
import com.robstore.core.hardware.camera.domain.repository.CameraRepository
import com.robstore.core.hardware.camera.presentation.viewModel.CameraViewModel
import com.robstore.core.store.local.DataStoreManager
import com.robstore.features.home.state.HomeScreen
import com.robstore.features.myApps.presentation.view.MyAppsScreen
import com.robstore.features.profile.presentation.view.ProfileScreen
import com.robstore.features.searchApp.presentation.viewModel.SearchAppScreen
import com.robstore.features.app.presentation.view.AppListScreen
import com.robstore.features.profile.di.UpdateUserAppModule.updateUserUseCase
import com.robstore.features.profile.presentation.viewModel.ProfileViewModelFactory


@Composable
fun Home(
    onNavigateToLogin: () -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    val headerColor = Color(0xFFf0f3f8) // o cualquier color que uses para tu header
    var currentScreen by remember { mutableStateOf<HomeScreen>(HomeScreen.AppList) }
    var isAppListContentShowing by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val cameraRepository: CameraRepository = CameraManager(context)


    val cameraViewModel = remember {
        CameraViewModel(cameraRepository, dataStoreManager)
    }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }





    SideEffect {
        systemUiController.setStatusBarColor(
            color = headerColor,
            darkIcons = false // o true según el contraste
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,


    ) {
        // --- Header ---
        if (currentScreen is HomeScreen.AppList && isAppListContentShowing) {
            Column(
                modifier = Modifier
                    .background(headerColor)
                    .fillMaxWidth()
                    .height(70.dp)
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2f

                        drawLine(
                            color = Color.LightGray, // El color de la línea
                            start = Offset(0f, y), // Punto de inicio: esquina inferior izquierda
                            end = Offset(size.width, y), // Punto final: esquina inferior derecha
                            strokeWidth = strokeWidth // El grosor de la línea
                        )
                    },
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de la aplicación",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 25.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .background(headerColor)
                .fillMaxWidth()
                .weight(1f),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center

        ) {

            when (currentScreen) {

                HomeScreen.AppList -> {
                    AppListScreen(
                        onBackToHomeDashboard = {  },
                        onSubScreenChanged = { isContentShowing ->
                            isAppListContentShowing = isContentShowing
                        }
                    )
                }

                HomeScreen.SearchApp -> {
                    SearchAppScreen(onBack = { currentScreen = HomeScreen.AppList })
                }
                HomeScreen.MyApps -> {
                    MyAppsScreen(onBack = { currentScreen = HomeScreen.AppList })
                }
                HomeScreen.Profile -> {
                    ProfileScreen(
                        onBack = { currentScreen = HomeScreen.AppList },
                        onLogout = onNavigateToLogin,
                        onUpdateSuccess = {},
                        profileViewModel = viewModel(
                            factory = ProfileViewModelFactory(
                                updateUserUseCase,
                                dataStoreManager,
                            )
                        ),
                        cameraViewModel = cameraViewModel,
                    )
                }
            }
        }


        Column(
            modifier = Modifier
                .background(color = Color(0xFFd4dff2))
                .fillMaxWidth()
                .height(55.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = { currentScreen = HomeScreen.AppList },
                    modifier = Modifier.size(60.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = "Inicio",
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                IconButton(
                    onClick = { currentScreen = HomeScreen.SearchApp },
                    modifier = Modifier.size(60.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = "Buscar",
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                IconButton(
                    onClick = { currentScreen = HomeScreen.MyApps },
                    modifier = Modifier.size(65.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Storage,
                            contentDescription = "Mis aplicaciones",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = "Mis Apps",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                IconButton(
                    onClick = { currentScreen = HomeScreen.Profile },
                    modifier = Modifier.size(60.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Perfil",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = "Perfil",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
