package org.ucb.iaestudio.ai.presentation.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import iaestudio.shared.generated.resources.Res
import iaestudio.shared.generated.resources.ic_sparkle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.ucb.iaestudio.ui.BoxWidget
import org.ucb.iaestudio.ui.IconBadge
import org.ucb.iaestudio.ui.ScreenHeader
import org.ucb.iaestudio.ui.borderlessFieldColors
import org.ucb.iaestudio.ui.formatAnswer

@Composable
fun SummaryScreen(viewModel: SummaryViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SummaryEffect.ShowToast -> println(effect.message)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(title = "Resumir", subtitle = "Pega tus apuntes y obtén las ideas clave")

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BoxWidget(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = state.value.text,
                    onValueChange = { viewModel.emitEvent(SummaryEvent.OnTextChanged(it)) },
                    placeholder = { Text("Pega aquí tu texto…") },
                    minLines = 7,
                    maxLines = 14,
                    colors = borderlessFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${state.value.text.length} caracteres",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.emitEvent(SummaryEvent.OnSummarize) },
                enabled = !state.value.isLoading,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(painter = painterResource(Res.drawable.ic_sparkle), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = "Resumir con IA", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp))
            }

            when {
                state.value.isLoading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                    Text("Resumiendo…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.value.error != null -> Text(
                    text = state.value.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
                state.value.summary.isNotEmpty() -> BoxWidget(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(Res.drawable.ic_sparkle, MaterialTheme.colorScheme.primary, size = 36)
                        Text(
                            text = "Resumen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = formatAnswer(state.value.summary))
                }
            }
        }
    }
}
