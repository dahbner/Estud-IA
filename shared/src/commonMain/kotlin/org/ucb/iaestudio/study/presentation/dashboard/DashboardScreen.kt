package org.ucb.iaestudio.study.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import iaestudio.shared.generated.resources.Res
import iaestudio.shared.generated.resources.ic_flag
import iaestudio.shared.generated.resources.ic_schedule
import iaestudio.shared.generated.resources.ic_sparkle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.ucb.iaestudio.study.domain.model.StudyStats
import org.ucb.iaestudio.study.domain.model.SubjectStat
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.ui.BoxWidget
import org.ucb.iaestudio.ui.IconBadge
import org.ucb.iaestudio.ui.ScreenHeader
import org.ucb.iaestudio.ui.formatAnswer
import org.ucb.iaestudio.ui.subjectColor

private val DoneColor = Color(0xFF10B981)

@Composable
fun DashboardScreen(
    onGoToSessions: () -> Unit,
    onGoToGoals: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashboardEffect.ShowToast -> println(effect.message)
            }
        }
    }

    val stats = state.value.stats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(eyebrow = "Estud-IA", title = "Tu progreso", subtitle = "Resumen de los últimos 7 días")

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeroProgress(stats)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStat(
                    icon = { IconBadge(Res.drawable.ic_schedule, Color(0xFF3B82F6)) },
                    value = stats.sessionCount.toString(),
                    label = "Sesiones",
                    modifier = Modifier.weight(1f)
                )
                SmallStat(
                    icon = { IconBadge(Res.drawable.ic_flag, MaterialTheme.colorScheme.primary) },
                    value = if (stats.hasGoals) stats.remainingMinutes.toDurationText() else "—",
                    label = "Te falta",
                    modifier = Modifier.weight(1f)
                )
            }

            if (stats.bySubject.isEmpty()) {
                StartWidget(onGoToGoals = onGoToGoals, onGoToSessions = onGoToSessions)
            } else {
                SubjectsWidget(stats.bySubject)
            }

            PlanWidget(
                state = state.value,
                onGeneratePlan = { viewModel.emitEvent(DashboardEvent.OnGeneratePlan) },
                onGoToGoals = onGoToGoals
            )
        }
    }
}

// Widget principal: tiempo de la semana contra la meta, con un anillo de progreso
@Composable
private fun HeroProgress(stats: StudyStats) {
    BoxWidget(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Tiempo estudiado",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stats.weekMinutes.toDurationText(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (stats.hasGoals) {
                        "de ${stats.goalMinutes.toDurationText()} de meta"
                    } else {
                        "Aún no defines tu meta"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { stats.progress },
                    modifier = Modifier.size(92.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 10.dp,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp
                )
                Text(
                    text = if (stats.hasGoals) "${(stats.progress * 100).toInt()}%" else "—",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SmallStat(icon: @Composable () -> Unit, value: String, label: String, modifier: Modifier = Modifier) {
    BoxWidget(modifier = modifier) {
        icon()
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Avance de cada materia contra su meta
@Composable
private fun SubjectsWidget(subjects: List<SubjectStat>) {
    BoxWidget(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Por materia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            subjects.forEach { subject ->
                val color = subjectColor(subject.subject)
                val isDone = subject.goalMinutes > 0 && subject.remainingMinutes == 0
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                        Text(
                            text = subject.subject,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f).padding(start = 10.dp)
                        )
                        Text(
                            text = when {
                                isDone -> "Cumplida"
                                subject.goalMinutes > 0 ->
                                    "${subject.minutes.toDurationText()} / ${subject.goalMinutes.toDurationText()}"
                                else -> "${subject.minutes.toDurationText()} · sin meta"
                            },
                            color = if (isDone) DoneColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                    LinearProgressIndicator(
                        progress = { subject.progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = color,
                        trackColor = color.copy(alpha = 0.12f),
                        strokeCap = StrokeCap.Round,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun StartWidget(onGoToGoals: () -> Unit, onGoToSessions: () -> Unit) {
    BoxWidget(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconBadge(Res.drawable.ic_flag, MaterialTheme.colorScheme.primary, size = 52)
            Text(text = "Empieza en 2 pasos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "1. Define tus metas por materia.\n2. Registra tus sesiones de estudio.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = onGoToGoals, shape = RoundedCornerShape(50)) {
                    Text("Definir metas", fontWeight = FontWeight.SemiBold)
                }
                FilledTonalButton(onClick = onGoToSessions, shape = RoundedCornerShape(50)) {
                    Text("Registrar", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// La IA recibe metas y avance, y devuelve prioridades + un plan de 3 días + una técnica
@Composable
private fun PlanWidget(state: DashboardUiState, onGeneratePlan: () -> Unit, onGoToGoals: () -> Unit) {
    BoxWidget(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(Res.drawable.ic_sparkle, MaterialTheme.colorScheme.primary, size = 44)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = "Plan con IA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Prioridades y un plan para tus próximos 3 días",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when {
            state.isPlanning -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                Text("Armando tu plan…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            state.error != null -> Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            state.plan.isNotEmpty() -> Text(
                text = formatAnswer(state.plan),
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(14.dp)
            )
            !state.stats.hasGoals -> Text(
                text = "Primero define tus metas para que la IA sepa qué priorizar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }

        if (state.stats.hasGoals) {
            Button(
                onClick = onGeneratePlan,
                enabled = !state.isPlanning,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(painter = painterResource(Res.drawable.ic_sparkle), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = if (state.plan.isEmpty()) "Generar mi plan" else "Actualizar plan",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        } else {
            FilledTonalButton(
                onClick = onGoToGoals,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Definir mis metas", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
