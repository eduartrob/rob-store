package com.robstore.features.authentication.recoveryPassword.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robstore.features.authentication.recoveryPassword.presentation.state.PasswordValidationState // Asume que tienes un estado de validación de contraseña

@Composable
fun SetNewPasswordDialog(
    newPasswordInput: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPasswordInput: String,
    onConfirmPasswordChange: (String) -> Unit,
    onUpdatePasswordClick: () -> Unit,
    onCancel: () -> Unit,
    newPasswordValidationState: PasswordValidationState?,
    confirmPasswordValidationState: PasswordValidationState?,
    isLoading: Boolean
) {
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Overlay semitransparente oscuro para el fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = false) { /* Consume clicks */ }, // Evita clicks en el fondo
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Ocupa el 90% del ancho
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Establecer Nueva Contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Ingresa tu nueva contraseña. Debe ser segura y fácil de recordar.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                // Campo Nueva Contraseña
                OutlinedTextField(
                    value = newPasswordInput,
                    onValueChange = onNewPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nueva Contraseña") },
                    isError = newPasswordValidationState is PasswordValidationState.Invalid || newPasswordValidationState is PasswordValidationState.Weak,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, errorContainerColor = Color.White,
                        focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, errorTextColor = Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                if (newPasswordValidationState is PasswordValidationState.Invalid) {
                    Text(text = "La contraseña no cumple los requisitos.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                } else if (newPasswordValidationState is PasswordValidationState.Weak) {
                    Text(text = "Contraseña débil.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = confirmPasswordInput,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirmar Contraseña") },
                    isError = confirmPasswordValidationState is PasswordValidationState.Mismatch,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, errorContainerColor = Color.White,
                        focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, errorTextColor = Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                if (confirmPasswordValidationState is PasswordValidationState.Mismatch) {
                    Text(text = "Las contraseñas no coinciden.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                // Botón para actualizar contraseña
                Button(
                    onClick = onUpdatePasswordClick,
                    enabled = !isLoading, // Deshabilitar mientras se carga
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F8B41),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Actualizar Contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Botón para cancelar
                TextButton(onClick = onCancel, enabled = !isLoading) {
                    Text(
                        text = "Cancelar",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
