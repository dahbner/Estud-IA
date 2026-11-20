package org.ucb.iaestudio.ai.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import iaestudio.shared.generated.resources.Res
import iaestudio.shared.generated.resources.ic_build
import iaestudio.shared.generated.resources.ic_refresh
import iaestudio.shared.generated.resources.ic_send
import iaestudio.shared.generated.resources.ic_sparkle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ui.CircleIconButton
import org.ucb.iaestudio.ui.IconBadge
import org.ucb.iaestudio.ui.ScreenHeader
import org.ucb.iaestudio.ui.borderlessFieldColors
import org.ucb.iaestudio.ui.formatAnswer

// Sugerencias pensadas para que el agente use sus herramientas
private val SUGGESTIONS = listOf(
    "¿Cuánto me falta esta semana?",
    "Hoy estudié 45 minutos de Cálculo",
    "Quiero estudiar 3 horas de Física por semana",
    "Explícame qué es una derivada"
)

// Nombre amigable de cada herramienta de Koog para mostrarlo bajo la respuesta
private fun toolLabel(name: String): String = when (name) {
    "consultar_progreso" -> "Consultó tu progreso"
    "registrar_sesion" -> "Registró una sesión"
    "definir_meta" -> "Guardó una meta"
    "listar_sesiones" -> "Revisó tus sesiones"
    else -> name
}

@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatEffect.ShowToast -> println(effect.message)
            }
        }
    }

    // Baja al último mensaje cuando llega uno nuevo
    LaunchedEffect(state.value.messages.size, state.value.isLoading, state.value.error) {
        val extraItem = if (state.value.isLoading || state.value.error != null) 1 else 0
        val lastIndex = state.value.messages.size + extraItem - 1
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        ScreenHeader(title = "Asistente IA", subtitle = "Consulta y registra tu estudio conversando") {
            CircleIconButton(
                icon = Res.drawable.ic_refresh,
                contentDescription = "Nuevo chat",
                onClick = { viewModel.emitEvent(ChatEvent.OnNewChat) }
            )
        }

        if (state.value.messages.isEmpty() && !state.value.isLoading) {
            EmptyChat(
                modifier = Modifier.weight(1f),
                onSuggestion = { viewModel.emitEvent(ChatEvent.OnSuggestion(it)) }
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.value.messages) { message ->
                    MessageBubble(message)
                }
                if (state.value.isLoading) {
                    item { ThinkingBubble() }
                }
                state.value.error?.let { error ->
                    item { Text(text = error, color = MaterialTheme.colorScheme.error) }
                }
            }
        }

        Composer(
            value = state.value.input,
            isLoading = state.value.isLoading,
            onValueChange = { viewModel.emitEvent(ChatEvent.OnInputChanged(it)) },
            onSend = { viewModel.emitEvent(ChatEvent.OnSend) }
        )
    }
}

// Caja de texto tipo píldora con el botón de enviar dentro
@Composable
private fun Composer(value: String, isLoading: Boolean, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 4.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Escribe tu pregunta…") },
            maxLines = 4,
            colors = borderlessFieldColors(),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (isLoading) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.primary
                )
                .clickable(enabled = !isLoading, onClick = onSend),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_send),
                contentDescription = "Enviar",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyChat(modifier: Modifier, onSuggestion: (String) -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconBadge(Res.drawable.ic_sparkle, MaterialTheme.colorScheme.primary, size = 64)
        Text(text = "¿En qué te ayudo hoy?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = "Elige una sugerencia o escribe tu propia pregunta.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SUGGESTIONS.forEach { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onSuggestion(suggestion) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = suggestion, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    if (message.isUser || message.toolsUsed.isEmpty()) {
        Bubble(message)
        return
    }
    // Respuesta del agente: debajo se ve qué herramientas de la app ejecutó
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Bubble(message)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            message.toolsUsed.forEach { tool ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_build),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = toolLabel(tool),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Bubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = if (message.isUser) AnnotatedString(message.text) else formatAnswer(message.text),
            color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    if (message.isUser) {
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 6.dp)
                    } else {
                        RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
                    }
                )
                .background(if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun ThinkingBubble() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Text("Pensando…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
