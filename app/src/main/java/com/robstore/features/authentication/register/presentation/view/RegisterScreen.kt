package com.robstore.features.authentication.register.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.robstore.R
import com.robstore.core.common.EmailValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.NameValidationState
import com.robstore.core.common.PasswordValidationState
import com.robstore.core.common.PhoneValidationState
import com.robstore.features.authentication.register.presentation.viewModel.RegisterViewModel

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,

) {
    val name: String by registerViewModel.nameInputText.collectAsState()
    val nameValidationState by registerViewModel.nameValidationState.collectAsState()

    val email: String by registerViewModel.emailInputText.collectAsState()
    val emailValidationState by registerViewModel.emailValidationState.collectAsState()

    val password: String by registerViewModel.passwordInputText.collectAsState()
    val passwordValidationState by registerViewModel.passwordValidationState.collectAsState()

    val confirmPassword: String by registerViewModel.confirmPasswordInputText.collectAsState()
    val confirmPasswordValidationState by registerViewModel.confirmPasswordValidationState.collectAsState()

    val phone: String by registerViewModel.phoneInputText.collectAsState()
    val phoneValidationState by registerViewModel.phoneValidationState.collectAsState()

    val generalUiState by registerViewModel.generalUiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }


    val passwordBorderColor by animateColorAsState(
        targetValue = if (passwordValidationState is PasswordValidationState.Invalid) Color.Red else Color(0xFFD4D4D4),
        animationSpec = tween(300)
    )
//
//    var checkPasswd by remember { mutableStateOf("")}

    val greyColor = Color(0xFF525252)
    val typedTextColor = Color.Black

    val scrollState = rememberScrollState()

    LaunchedEffect(generalUiState) {
        if (generalUiState is GeneralUiState.Success) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            )  {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Mi icono personalizado",
                    modifier = Modifier
                        .size(50.dp)

                )
                Text(
                    text = "Rob Store",
                    fontSize = 28.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Crear una cuenta",
                    fontSize = 20.sp,
                    color = greyColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            )  {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Nombre de usuario",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { registerViewModel.onNameChange(it)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                registerViewModel.onNameFocusChanged(focus.isFocused)
                            },
                        placeholder = { Text("John") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = nameValidationState != null && nameValidationState !is NameValidationState.Valid,
                        colors = OutlinedTextFieldDefaults.colors(
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            errorContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Black
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
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Correo electrónico",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { registerViewModel.onEmailChange(it)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                registerViewModel.onEmailFocusChanged(focus.isFocused)
                            },
                        placeholder = { Text("example@gmail.com") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = emailValidationState != null && emailValidationState !is EmailValidationState.Valid,
                        colors = OutlinedTextFieldDefaults.colors(
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            errorContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Black
                        ),

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    when (emailValidationState) {
                        is EmailValidationState.Empty -> Text(
                            text = "El correo electrónico es obligatorio.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is EmailValidationState.Invalid -> Text(
                            text = "El formato del correo no es válido.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is EmailValidationState.Error -> Text(
                            text = "Error con el correo.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        else -> {}
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ){
                    Text(
                        text = "Contraseña",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { registerViewModel.onPasswordChange(it)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                registerViewModel.onPasswordFocusChanged(focus.isFocused)
                            },
                        placeholder = { Text("********") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = passwordValidationState != null && passwordValidationState !is PasswordValidationState.Valid,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0073ED),
                            unfocusedBorderColor = passwordBorderColor,
                            disabledBorderColor = passwordBorderColor,
                            errorBorderColor = Color.Red,
                            focusedTextColor = typedTextColor,
                            unfocusedTextColor = typedTextColor,
                            disabledTextColor = Color.LightGray,
                            errorTextColor = typedTextColor,
                            focusedLabelColor = Color(0xFF0073ED),
                            unfocusedLabelColor = Color.Gray,
                            disabledLabelColor = Color.LightGray,
                            errorLabelColor = Color.Red,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),

                        )

                    when (passwordValidationState) {
                        is PasswordValidationState.Empty -> Text(
                            text = "La contraseña es necesaria",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is PasswordValidationState.TooShort -> Text(
                            text = "La contraseña es muy corta",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is PasswordValidationState.Invalid -> Text(
                            text = "La contraseña es invalida",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        else -> {}
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Confirmar contraseña",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { registerViewModel.onConfirmPasswordChange(it)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                registerViewModel.onConfirmPasswordFocusChanged(focus.isFocused)
                            },
                        placeholder = { Text("********") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = confirmPasswordValidationState != null && confirmPasswordValidationState !is PasswordValidationState.Valid,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0073ED),
                            unfocusedBorderColor = passwordBorderColor,
                            disabledBorderColor = passwordBorderColor,
                            errorBorderColor = Color.Red,
                            focusedTextColor = typedTextColor,
                            unfocusedTextColor = typedTextColor,
                            disabledTextColor = Color.LightGray,
                            errorTextColor = typedTextColor,
                            focusedLabelColor = Color(0xFF0073ED),
                            unfocusedLabelColor = Color.Gray,
                            disabledLabelColor = Color.LightGray,
                            errorLabelColor = Color.Red,
                        ),

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    when (passwordValidationState) {
                        is PasswordValidationState.Empty -> Text(
                            text = "La contraseña es necesaria",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is PasswordValidationState.Mismatch -> Text(
                            text = "Las contraseñas no coinciden.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        else -> {}
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Telefono",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { registerViewModel.onPhoneChange(it)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                registerViewModel.onPhoneFocusChanged(focus.isFocused)
                            },
                        placeholder = { Text("1234567890") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = phoneValidationState != null && phoneValidationState !is PhoneValidationState.Valid,
                        colors = OutlinedTextFieldDefaults.colors(
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            errorContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Black
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

                }

                Button(
                    onClick = { registerViewModel.validateCredentials()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(vertical = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F8B41),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                )  {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "¿Ya tienes una cuenta?",
                        fontSize = 20.sp,
                        color = greyColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 18.sp,
                        color = Color(0xFF3f8b41),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onNavigateToLogin()
                        }

                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,

                    ){
                    Text(
                        text = "Al registrarte, aceptas nuestros Términos y Condiciones y Política de Privacidad",
                        fontSize = 12.sp,
                        color = Color(0xFF737373),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

            }
        }
    }

    AnimatedVisibility(
        visible = generalUiState is GeneralUiState.Loading,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(54.dp),
                color = Color.White
            )
        }
    }


}

