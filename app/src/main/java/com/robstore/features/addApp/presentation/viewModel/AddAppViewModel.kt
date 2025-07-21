package com.robstore.features.addApp.presentation.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddAppViewModel {
    private val _nameInputText = MutableStateFlow("")
    val nameInputText: StateFlow<String> = _nameInputText

    private val _descriptionInputText = MutableStateFlow("")
    val descriptionInputText: StateFlow<String> = _descriptionInputText

    private val _versionInputText = MutableStateFlow("")
    val versionInputText: StateFlow<String> = _versionInputText

    private val _routeInputText = MutableStateFlow("")
    val routeInputText: StateFlow<String> = _routeInputText

    private val _sizeInputText = MutableStateFlow("")
    val sizeInputText: StateFlow<String> = _sizeInputText

    private val _photoApp = MutableStateFlow<String?>(null)
    val photoApp: StateFlow<String?> = _photoApp

}