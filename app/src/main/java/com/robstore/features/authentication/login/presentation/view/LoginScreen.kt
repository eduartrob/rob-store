package com.robstore.features.authentication.login.presentation.view

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

import com.robstore.R
import com.robstore.core.common.EmailValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.PasswordValidationState
import com.robstore.features.authentication.login.di.AppModule
import com.robstore.features.authentication.login.presentation.viewModel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest


@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecoveryPasswd: () -> Unit,
) {
    val email: String by loginViewModel.emailInputText.collectAsState()
    val emailValidationState by loginViewModel.emailValidationState.collectAsState()

    val password: String by loginViewModel.password.collectAsState()
    val passwordValidatioinState by loginViewModel.passwordValidationState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }


    val passwordBorderColor by animateColorAsState(
        targetValue = if (passwordValidatioinState is PasswordValidationState.Invalid) Color.Red else Color(0xFFD4D4D4),
        animationSpec = tween(300)
    )

    val greyColor = Color(0xFF525252)
    val greenColor = Color(0xFF4cb050)
    val typedTextColor = Color.Black



    val uiState by loginViewModel.loginUiState.collectAsState()




    LaunchedEffect(key1 = uiState) {
        if (uiState is GeneralUiState.Success) {
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
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de Rob Store",
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
                    text = "Inicia sesión para continuar",
                    fontSize = 20.sp,
                    color = greyColor,
                    fontWeight = FontWeight.Medium
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Correo electrónico",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { loginViewModel.onEmailChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                loginViewModel.onEmailFocusChanged(focus.isFocused)
                            },
                        placeholder = { Text("example@gmail.com") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = emailValidationState != null && (
                                emailValidationState is EmailValidationState.Error ||
                                        emailValidationState is EmailValidationState.Invalid ||
                                        emailValidationState is EmailValidationState.Empty ||
                                        emailValidationState is EmailValidationState.NotRegistered
                                ),
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

                        is EmailValidationState.NotRegistered -> Text(
                            text = "El correo no está registrado.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )

                        else -> {}
                    }
                }

                Spacer(Modifier.height(10.dp))


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Contraseña",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { loginViewModel.onPasswordChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("********") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = passwordValidatioinState != null &&
                                (passwordValidatioinState is PasswordValidationState.Invalid),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description =
                                if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
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
                    when (passwordValidatioinState) {
                        is PasswordValidationState.Invalid -> Text(
                            text = "El correo o la contraseña son inválidos",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )

                        else -> {}
                    }
                }

                Button(
                    onClick = { loginViewModel.validateCredentials() },
                    enabled = uiState !is GeneralUiState.Loading,
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
                    Text(text = "Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 18.sp,
                        color = greenColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(enabled = uiState !is GeneralUiState.Loading) {
                            onNavigateToRecoveryPasswd()
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "¿No tienes cuenta?",
                        fontSize = 20.sp,
                        color = greyColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Crear cuenta",
                        fontSize = 18.sp,
                        color = Color(0xFF3f8b41),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(enabled = uiState !is GeneralUiState.Loading) {
                            onNavigateToRegister()
                        }
                    )
                }
            }
        }


        AnimatedVisibility(
            visible = uiState is GeneralUiState.Loading,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(54.dp),
                    color = Color.Black
                )
            }
        }


        AnimatedVisibility(
            visible = uiState is GeneralUiState.Error,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            val errorMessage = (uiState as? GeneralUiState.Error)?.message ?: "Error desconocido."

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Conexión Fallida",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = errorMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { loginViewModel.onRetryConnection() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F8B41),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Reintentar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


