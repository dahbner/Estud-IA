package org.ucb.iaestudio.study.presentation.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import iaestudio.shared.generated.resources.ic_flag
import iaestudio.shared.generated.resources.ic_remove
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.ucb.iaestudio.study.domain.model.SubjectGoal
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.ui.BoxWidget
import org.ucb.iaestudio.ui.IconBadge
import org.ucb.iaestudio.ui.ScreenHeader
import org.ucb.iaestudio.ui.subjectColor

private val GOAL_OPTIONS = listOf(60, 120, 180, 240, 300, 360, 480, 600)

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GoalsEffect.ShowToast -> println(effect.message)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ScreenHeader(title = "Metas", subtitle = "Define cuántas horas quieres estudiar por semana")
            }
            item {
                TotalWidget(totalMinutes = state.value.totalMinutes, subjects = state.value.goals.size)
            }
            if (state.value.goals.isEmpty()) {
                item { EmptyGoals() }
            } else {
                items(state.value.goals, key = { it.subject }) { goal ->
                    GoalRow(
                        goal = goal,
                        onIncrease = { viewModel.emitEvent(GoalsEvent.OnIncrease(goal.subject)) },
                        onDecrease = { viewModel.emitEvent(GoalsEvent.OnDecrease(goal.subject)) },
                        onDelete = { viewModel.emitEvent(GoalsEvent.OnDelete(goal.subject)) }
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { viewModel.emitEvent(GoalsEvent.OnAddClick) },
            icon = { Icon(painter = painterResource(Res.drawable.ic_add), contentDescription = null) },
            text = { Text("Nueva meta", fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        )

        if (state.value.isSheetOpen) {
            AddGoalSheet(
                state = state.value,
                onSubjectChanged = { viewModel.emitEvent(GoalsEvent.OnSubjectChanged(it)) },
                onMinutesSelected = { viewModel.emitEvent(GoalsEvent.OnMinutesSelected(it)) },
                onSave = { viewModel.emitEvent(GoalsEvent.OnSave) },
                onDismiss = { viewModel.emitEvent(GoalsEvent.OnDismissSheet) }
            )
        }
    }
}

@Composable
private fun TotalWidget(totalMinutes: Int, subjects: Int) {
    BoxWidget(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(Res.drawable.ic_flag, MaterialTheme.colorScheme.primary, size = 52)
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = "Meta semanal total",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (totalMinutes == 0) "Sin metas" else totalMinutes.toDurationText(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Suma de $subjects ${if (subjects == 1) "materia" else "materias"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyGoals() {
    Text(
        text = "Agrega las materias en las que quieres esforzarte y cuántas horas por semana. " +
            "Las materias sin meta no cuentan para tu objetivo.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 24.dp)
    )
}

@Composable
private fun GoalRow(goal: SubjectGoal, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
    val color = subjectColor(goal.subject)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = goal.subject.take(1), fontWeight = FontWeight.Bold, color = color)
        }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(text = goal.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = "por semana",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Control - / + para ajustar la meta rápido
        StepButton(Res.drawable.ic_remove, "Bajar meta", onDecrease)
        Text(
            text = goal.weeklyMinutes.toDurationText(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 64.dp)
        )
        StepButton(Res.drawable.ic_add, "Subir meta", onIncrease)
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = "Eliminar meta",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StepButton(icon: DrawableResource, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddGoalSheet(
    state: GoalsUiState,
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
            Text(text = "Nueva meta", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = state.subject,
                onValueChange = onSubjectChanged,
                label = { Text("Materia") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (state.suggestedSubjects.isNotEmpty()) {
                Text(
                    text = "De tus sesiones",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.suggestedSubjects.forEach { subject ->
                        SuggestionChip(onClick = { onSubjectChanged(subject) }, label = { Text(subject) })
                    }
                }
            }

            Text(
                text = "Horas por semana",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GOAL_OPTIONS.forEach { minutes ->
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
                Text("Guardar meta", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
