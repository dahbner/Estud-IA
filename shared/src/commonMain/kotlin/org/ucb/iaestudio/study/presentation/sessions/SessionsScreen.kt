package org.ucb.iaestudio.study.presentation.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import iaestudio.shared.generated.resources.Res
import iaestudio.shared.generated.resources.ic_add
import iaestudio.shared.generated.resources.ic_delete
import iaestudio.shared.generated.resources.ic_schedule
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.ui.BoxWidget
import org.ucb.iaestudio.ui.IconBadge
import org.ucb.iaestudio.ui.ScreenHeader
import org.ucb.iaestudio.ui.subjectColor

private val DURATION_OPTIONS = listOf(15, 30, 45, 60, 90, 120)

@Composable
fun SessionsScreen(viewModel: SessionsViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SessionsEffect.ShowToast -> println(effect.message)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Sesiones",
                subtitle = if (state.value.sessions.isEmpty()) {
                    "Registra tu tiempo de estudio"
                } else {
                    "${state.value.sessions.size} sesiones registradas"
                }
            )

            if (state.value.sessions.isEmpty()) {
                EmptySessions()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.value.sessions, key = { it.id }) { session ->
                        SessionRow(
                            session = session,
                            onDelete = { viewModel.emitEvent(SessionsEvent.OnDelete(session.id)) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { viewModel.emitEvent(SessionsEvent.OnAddClick) },
            icon = { Icon(painter = painterResource(Res.drawable.ic_add), contentDescription = null) },
            text = { Text("Registrar", fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        )

        if (state.value.isDialogOpen) {
            AddSessionSheet(
                state = state.value,
                onSubjectChanged = { viewModel.emitEvent(SessionsEvent.OnSubjectChanged(it)) },
                onMinutesSelected = { viewModel.emitEvent(SessionsEvent.OnMinutesSelected(it)) },
                onSave = { viewModel.emitEvent(SessionsEvent.OnSave) },
                onDismiss = { viewModel.emitEvent(SessionsEvent.OnDismissDialog) }
            )
        }
    }
}

@Composable
private fun EmptySessions() {
    BoxWidget(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconBadge(Res.drawable.ic_schedule, MaterialTheme.colorScheme.primary, size = 56)
            Text(text = "Sin sesiones todavía", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "Toca \"Registrar\" para anotar tu primera sesión de estudio.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SessionRow(session: SessionItem, onDelete: () -> Unit) {
    val color = subjectColor(session.subject)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadrado con la inicial de la materia, en el color de esa materia
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = session.subject.take(1), fontWeight = FontWeight.Bold, color = color)
        }
        Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
            Text(text = session.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${session.durationText} · ${session.timeAgoText}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = "Eliminar sesión",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// Formulario en una hoja que sube desde abajo
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddSessionSheet(
    state: SessionsUiState,
    onSubjectChanged: (String) -> Unit,
    onMinutesSelected: (Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Nueva sesión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = state.subject,
                onValueChange = onSubjectChanged,
                label = { Text("Materia") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (state.knownSubjects.isNotEmpty()) {
                Text(text = "Tus materias", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.knownSubjects.forEach { subject ->
                        SuggestionChip(onClick = { onSubjectChanged(subject) }, label = { Text(subject) })
                    }
                }
            }

            Text(text = "Duración", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DURATION_OPTIONS.forEach { minutes ->
                    FilterChip(
                        selected = state.minutes == minutes,
                        onClick = { onMinutesSelected(minutes) },
                        label = { Text(minutes.toDurationText()) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            state.formError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onSave,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Guardar sesión", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
