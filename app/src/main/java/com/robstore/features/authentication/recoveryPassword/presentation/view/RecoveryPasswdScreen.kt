package com.robstore.features.authentication.recoveryPassword.presentation.view

import VerificationCodeDialog
import android.util.Patterns
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator

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

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.robstore.R
import com.robstore.features.authentication.recoveryPassword.presentation.state.EmailValidationState
import com.robstore.features.authentication.recoveryPassword.presentation.state.PasswordValidationState
import com.robstore.features.authentication.recoveryPassword.presentation.state.RecoveryUiState
import com.robstore.features.authentication.recoveryPassword.presentation.viewModel.RecoveryPasswdViewModel
import kotlinx.coroutines.delay

@Composable
fun RecoveryPasswd(
    recoveryPasswdViewModel: RecoveryPasswdViewModel,
    onNavigateToLogin: () -> Unit,
) {
    val email: String by recoveryPasswdViewModel.emailInputText.collectAsState()
    val emailValidationState by recoveryPasswdViewModel.emailValidationState.collectAsState()


    var isEmailValid by remember { mutableStateOf(true) }
    var hasFocus by remember { mutableStateOf(false) }
    var wasFocusedOnce by remember { mutableStateOf(false) }

    // Estados para el pop-up de verificación
    val verificationCode: String by recoveryPasswdViewModel.verificationCodeInput.collectAsState()
    val remainingSeconds: Int by recoveryPasswdViewModel.remainingSeconds.collectAsState()
    val verificationCodeError: String? by recoveryPasswdViewModel.verificationCodeError.collectAsState()

    // Estados para el pop-up de nueva contraseña
    val newPasswordInput: String by recoveryPasswdViewModel.newPasswordInput.collectAsState()
    val confirmPasswordInput: String by recoveryPasswdViewModel.confirmPasswordInput.collectAsState()
    val newPasswordValidationState: PasswordValidationState? by recoveryPasswdViewModel.newPasswordValidationState.collectAsState()
    val confirmPasswordValidationState: PasswordValidationState? by recoveryPasswdViewModel.confirmPasswordValidationState.collectAsState()


    // Función de validación
    fun validateEmail(input: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    // Color animado del borde
    val borderColor by animateColorAsState(
        targetValue = if (!isEmailValid && wasFocusedOnce) Color.Red else Color(0xFFD4D4D4),
        animationSpec = tween(durationMillis = 300)
    )

    val greyColor = Color(0xFF525252)

    val uiState by recoveryPasswdViewModel.recoveryUiState.collectAsState()

//    LaunchedEffect(key1 = Unit) {
//        recoveryPasswdViewModel.navigateToLoginAfterPasswordUpdate.collectLatest {
//
//        }
//    }

    LaunchedEffect(uiState) {
        if (uiState is RecoveryUiState.PasswordUpdateSuccess) {
            delay(2000)
            onNavigateToLogin()
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
                    text = "Recuperación de contraseña",
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
            ) {
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
                        onValueChange = { recoveryPasswdViewModel.onEmailChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                recoveryPasswdViewModel.onEmailFocusChanged(focus.isFocused)
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
                    // Muestra el mensaje de error del email
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
                            text = "El correo es incorrecto o no existe.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )

                        else -> {}
                    }


                }
                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = { recoveryPasswdViewModel.validateCredentials() },
                    enabled = uiState !is RecoveryUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F8B41),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Enviar correo de recuperación",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "¿Recordaste tu contraseña?",
                        fontSize = 18.sp,
                        color = greyColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        color = Color(0xFF3f8b41),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onNavigateToLogin()
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState is RecoveryUiState.Loading,
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

        // --- CODE VERIFICATION DIALOG ---
        AnimatedVisibility(
            visible = uiState is RecoveryUiState.CodeSent,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            VerificationCodeDialog(
                remainingSeconds = remainingSeconds,
                onCodeChange = { recoveryPasswdViewModel.onVerificationCodeChange(it) },
                onVerifyClick = { recoveryPasswdViewModel.verifyCode() },
                onResendCodeClick = { recoveryPasswdViewModel.resendCode() },
                onCancel = { recoveryPasswdViewModel.cancelVerification() },
                verificationCode = verificationCode,
                verificationCodeError = verificationCodeError
            )
        }

        // --- DIALOG TO SET NEW PASSWORD ---
        AnimatedVisibility(
            visible = uiState is RecoveryUiState.CodeVerified,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            SetNewPasswordDialog(
                newPasswordInput = newPasswordInput,
                onNewPasswordChange = { recoveryPasswdViewModel.onNewPasswordChange(it) },
                confirmPasswordInput = confirmPasswordInput,
                onConfirmPasswordChange = { recoveryPasswdViewModel.onConfirmPasswordChange(it) },
                onUpdatePasswordClick = { recoveryPasswdViewModel.updatePassword() },
                onCancel = { recoveryPasswdViewModel.cancelVerification() },
                newPasswordValidationState = newPasswordValidationState,
                confirmPasswordValidationState = confirmPasswordValidationState,
                isLoading = uiState is RecoveryUiState.Loading
            )
        }

        // --- PARTE CLAVE 2: AnimatedVisibility que muestra el SuccessMessageOverlay ---
        // Este bloque se activa cuando el uiState es PasswordUpdateSuccess
        AnimatedVisibility(
            visible = uiState is RecoveryUiState.PasswordUpdateSuccess, // <-- La visibilidad depende de este estado
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            // Se pasa el mensaje del estado PasswordUpdateSuccess al Composable
            SuccessMessageOverlay(message = (uiState as RecoveryUiState.PasswordUpdateSuccess).message) // <-- Aquí se llama al Composable del mensaje de éxito
        }

    }
}

