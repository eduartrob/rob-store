package com.robstore.features.profile.presentation.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.robstore.R
import com.robstore.core.common.EmailValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.NameValidationState
import com.robstore.core.common.PhoneValidationState
import com.robstore.core.hardware.camera.presentation.CameraLauncherManager
import com.robstore.core.hardware.camera.presentation.viewModel.CameraViewModel
import com.robstore.features.profile.presentation.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUpdateSuccess: () -> Unit,
    profileViewModel: ProfileViewModel,
    cameraViewModel: CameraViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val name by profileViewModel.nameInputText.collectAsState()
    val nameValidationState by profileViewModel.nameValidationState.collectAsState()
    val email by profileViewModel.emailInputText.collectAsState()
    val emailValidationState by profileViewModel.emailValidationState.collectAsState()
    val phone by profileViewModel.phoneInputText.collectAsState()
    val phoneValidationState by profileViewModel.phoneValidationState.collectAsState()
    val generalUiState by profileViewModel.generalUiState.collectAsState()
    val capturedImageUri by profileViewModel.photoUri.collectAsState()
    val region: String by profileViewModel.regionInputText.collectAsState()



    var showPhotoOptionsDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    LaunchedEffect(generalUiState) {
        when (generalUiState) {
            is GeneralUiState.Success -> {
                onUpdateSuccess()
            }
            is GeneralUiState.Error -> {}
            else -> {}
        }
    }

    val cameraLauncher = CameraLauncherManager(
        cameraViewModel = cameraViewModel,
        coroutineScope = coroutineScope,
        onPhotoCaptured = { uri -> profileViewModel.onImageSelected(
            uri,
            context = context
        ) }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileViewModel.onImageSelected(it, context) // aquí ya lo pasas
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf0f3f8))
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start

        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Spacer(modifier = Modifier.weight(0.15f))
        }
        Spacer(modifier = Modifier.height(10.dp))




        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 0.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!capturedImageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = capturedImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Foto de perfil por defecto",
                            tint = Color.DarkGray,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .padding(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { showPhotoOptionsDialog = true },
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 6.dp, y = 2.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,

                            contentDescription = "Editar foto de perfil",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Mi Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF525252),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { profileViewModel.onNameChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .onFocusChanged { focus ->
                            profileViewModel.onNameFocusChanged(focus.isFocused)
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    shape = RoundedCornerShape(10.dp),
                    isError = nameValidationState != null && nameValidationState !is NameValidationState.Valid,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFd3d3d3),
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                when (nameValidationState) {
                    is NameValidationState.Empty -> Text(
                        text = "El nombre es obligatorio.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is NameValidationState.TooShort -> Text(
                        text = "El nombre es muy corto.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is NameValidationState.TooLong -> Text(
                        text = "El nombre es demasiado largo.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is NameValidationState.InvalidCharacters -> Text(
                        text = "El nombre tiene caracteres invalidos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is NameValidationState.AlreadyTaken -> Text(
                        text = "El nombre ya esta en uso.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    else -> {}
                }


                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF525252),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center // Centra el texto del label
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { profileViewModel.onEmailChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .onFocusChanged { focus ->
                            profileViewModel.onEmailFocusChanged(focus.isFocused)
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    //readOnly = true, // Hacerlo de solo lectura para el preview
                    shape = RoundedCornerShape(10.dp),
                    isError = emailValidationState != null && emailValidationState !is EmailValidationState.Valid,
                    colors = OutlinedTextFieldDefaults.colors(
                        //focusedBorderColor = Color(0xFF525252), // Color del borde cuando está enfocado
                        unfocusedBorderColor = Color(0xFFd3d3d3), // Color del borde cuando no está enfocado
                        //disabledBorderColor = Color(0xFF525252), // Color del borde cuando está deshabilitado
                        focusedContainerColor = Color.Transparent, // Color de fondo del campo cuando está enfocado
//                    unfocusedContainerColor = Color.White, // Color de fondo del campo cuando no está enfocado
//                    disabledContainerColor = Color.White, // Color de fondo del campo cuando está deshabilitado
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Text(
                    text = "Teléfono",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF525252),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center // Centra el texto del label
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { profileViewModel.onPhoneChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .onFocusChanged { focus ->
                            profileViewModel.onPhoneFocusChanged(focus.isFocused)
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    shape = RoundedCornerShape(10.dp),
                    isError = phoneValidationState != null && phoneValidationState !is PhoneValidationState.Valid,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFd3d3d3),
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                when (phoneValidationState) {
                    is PhoneValidationState.Empty -> Text(
                        text = "El telefono es necesario.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is PhoneValidationState.InvalidFormat -> Text(
                        text = "Formato de teléfono inválido. Solo se permiten dígitos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is PhoneValidationState.TooShort -> Text(
                        text = "El número de teléfono es demasiado corto. Debe tener 10 dígitos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is PhoneValidationState.TooLong -> Text(
                        text = "El número de teléfono es demasiado largo. Debe tener 10 dígitos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    is PhoneValidationState.Error -> Text(
                        text = "Ocurrió un error con el número de teléfono.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    else -> {}
                }

                Text(
                    text = "Región",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF525252),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = region,
                    onValueChange = { /* No permitir cambios si es de solo lectura */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    readOnly = true, // ¡CAMBIO AQUÍ! Ahora es de solo lectura
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFd3d3d3),
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Black, // Color para texto deshabilitado
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )



                Button(
                    onClick = {
                        coroutineScope.launch {
                            profileViewModel.updateCredentials()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007aff),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Guardar cambios", fontWeight = FontWeight.Bold)
                }
            }
        }





        Spacer(modifier = Modifier.height(10.dp))



        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFe5f2ff)),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 18.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Recuperación de cuenta",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003166),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Introduce tu correo electrónico para recibir un enlace de recuperación",
                    fontSize = 16.sp,
                    color = Color(0xFF3974b3),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {  },
                    modifier = Modifier
                        .fillMaxWidth(),
                        //.onFocusChanged(onEmailFocusChanged),
                    placeholder = { Text("correo@ejemplo.com", color = Color(0xFF94a3b8) ) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
//                    isError = emailValidationState != null && (
//                            emailValidationState is MockEmailValidationState.Error ||
//                                    emailValidationState is MockEmailValidationState.Invalid ||
//                                    emailValidationState is MockEmailValidationState.Empty ||
//                                    emailValidationState is MockEmailValidationState.NotRegistered
//                            ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFd3d3d3),
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )
                // Muestra el mensaje de error del email
//                when (emailValidationState) {
//                    is MockEmailValidationState.Empty -> Text(
//                        text = "El correo electrónico es obligatorio.",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    is MockEmailValidationState.Invalid -> Text(
//                        text = "El formato del correo no es válido.",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    is MockEmailValidationState.NotRegistered -> Text(
//                        text = "El correo es incorrecto o no existe.",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    else -> {}
//                }

                Button(
                    onClick = {},
                    //enabled = !isLoading, // Deshabilita el botón si está cargando
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0073ED), // Color azul para el botón de enviar
                        contentColor = Color.White
                    )
                ) {
//                    if (isLoading) {
//                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
//                    } else {
                        Text("Enviar", fontWeight = FontWeight.Bold)
                    //}
                }
            }
        }








        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        profileViewModel.logout()
                        onLogout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cerrar sesión", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPhotoOptionsDialog) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(0.95f),
            onDismissRequest = {showPhotoOptionsDialog = false},
            title = { Text("Cambiar foto", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    TextButton(onClick = {
                        showPhotoOptionsDialog = false
                        galleryLauncher.launch("image/*")
                    }){
                        Text("Elegir foto")
                    }
                    TextButton(onClick = {
                        showPhotoOptionsDialog = false
                        cameraLauncher.launchCamera()
                    }) {
                        Text("Tomar foto")
                    }
                    TextButton(onClick = {
                        showPhotoOptionsDialog = false
                        profileViewModel.clearProfileImage()
                    }) {
                        Text("Eliminar la foto actual", color = Color.Red)
                    }
                }
            },
            confirmButton = {
                // No se necesita un botón de confirmación si las acciones están en los TextButtons
            },
            dismissButton = {
                // Un botón explícito de cancelar si lo deseas, aunque onDismissRequest ya lo maneja
                TextButton(onClick = {showPhotoOptionsDialog = false}) {
                    Text("Cancelar")
                }
            }
        )
    }
}