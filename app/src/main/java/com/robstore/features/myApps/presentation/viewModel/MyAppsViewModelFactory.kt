package com.robstore.features.myApps.presentation.viewModel

import android.content.Context // Importar Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.features.myApps.domain.useCase.MyAppsUseCase // Importa tu MyAppsUseCase

class MyAppsViewModelFactory(
    private val myAppsUseCase: MyAppsUseCase, // La dependencia que el ViewModel necesita
    private val applicationContext: Context // <-- ¡Añadido aquí!
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyAppsViewModel::class.java)) {
            return MyAppsViewModel(
                myAppsUseCase,
                applicationContext = applicationContext // <-- ¡Pasado aquí!
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
