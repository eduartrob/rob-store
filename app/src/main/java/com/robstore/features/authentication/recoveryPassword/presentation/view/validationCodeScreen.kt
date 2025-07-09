import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VerificationCodeDialog(
    remainingSeconds: Int, // Recibe los segundos restantes del ViewModel
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onCancel: () -> Unit, // Para cerrar el diálogo si el usuario quiere volver al login
    onResendCodeClick: () -> Unit,
    verificationCode: String, // El valor actual del código de verificación
    verificationCodeError: String? = null // Mensaje de error para el código
) {
    // Overlay semitransparente oscuro para el fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // Un poco más oscuro para un pop-up
            .clickable(enabled = false) { /* Consume clicks para que no interactúe con el fondo */ },
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
                    text = "Código de Verificación Enviado",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Hemos enviado un código de 6 dígitos a tu correo electrónico. Por favor, ingrésalo a continuación.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                // Contador de tiempo
                Text(
                    text = "Tiempo restante: ${String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (remainingSeconds <= 30) Color.Red else Color(0xFF3F8B41) // Rojo cuando queden pocos segundos
                )

                // Campo para ingresar el código
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = onCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Código de 6 dígitos") },
                    isError = !verificationCodeError.isNullOrEmpty(),
                    supportingText = {
                        if (!verificationCodeError.isNullOrEmpty()) {
                            Text(text = verificationCodeError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Black,
                        errorBorderColor = Color.Red
                    )
                )

                // Botón para verificar el código
                Button(
                    onClick = onVerifyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F8B41),
                        contentColor = Color.White
                    )
                ) {
                    Text("Verificar Código", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

//                // Opción para reenviar código
                TextButton( onClick = (onResendCodeClick)) {
                    Text(
                        text = "Reenviar Código",
                        fontSize = 16.sp,
                        color = Color(0xFF0073ED),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Botón para cancelar y volver a la pantalla de login
                TextButton(onClick = onCancel) {
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