package com.robstore.features.app.presentation.view


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.robstore.features.app.domain.model.AppInfo
import kotlinx.coroutines.flow.collectLatest


@Composable
fun AppCard(app: AppInfo, onClick: (AppInfo) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    // 2. Crear un estado para saber si la tarjeta está siendo presionada
    var isPressed by remember { mutableStateOf(false) }

    // 3. Observar las interacciones para actualizar el estado isPressed
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release -> isPressed = false
                is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val cardBackgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFe3e8ed) else Color.White, // Color cuando está presionado, o blanco normal
        label = "cardBackgroundColorAnimation"
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 1.dp, vertical = 1.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick(app)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = app.iconUrl),
                contentDescription = "${app.name} icon",
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información de la aplicación (nombre y descripción)
            Column(modifier = Modifier.weight(1f)) { // El Column toma el espacio restante
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.headlineSmall, // Estilo de texto del tema
                    fontWeight = FontWeight.W500,
                    fontSize = 15.5.sp,
                    color = Color.Black // Color del texto del nombre

                )
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Color.Gray, // Color del texto de la descripción
                    maxLines = 1, // Limita la descripción a 2 líneas
                    overflow = TextOverflow.Ellipsis // Añade puntos suspensivos si el texto es muy largo
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (4).dp)
                ) {
                    Text(
                        text = app.rate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rate",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Yellow
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = app.size,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }
    }
}
