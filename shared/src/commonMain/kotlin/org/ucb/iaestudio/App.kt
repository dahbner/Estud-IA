package org.ucb.iaestudio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import iaestudio.shared.generated.resources.Res
import iaestudio.shared.generated.resources.ic_chat
import iaestudio.shared.generated.resources.ic_flag
import iaestudio.shared.generated.resources.ic_home
import iaestudio.shared.generated.resources.ic_notes
import iaestudio.shared.generated.resources.ic_schedule
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.ucb.iaestudio.ai.presentation.chat.ChatScreen
import org.ucb.iaestudio.ai.presentation.summary.SummaryScreen
import org.ucb.iaestudio.navigation.Screen
import org.ucb.iaestudio.study.presentation.goals.GoalsScreen
import org.ucb.iaestudio.study.presentation.dashboard.DashboardScreen
import org.ucb.iaestudio.study.presentation.sessions.SessionsScreen

private val AppColors = lightColorScheme(
    primary = Color(0xFFE53935),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDECEC),
    onPrimaryContainer = Color(0xFF8E1B1B),
    secondaryContainer = Color(0xFFFDECEC),
    onSecondaryContainer = Color(0xFF8E1B1B),
    background = Color(0xFFF2F2F5),
    onBackground = Color(0xFF1C1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFFF2F2F5),
    onSurfaceVariant = Color(0xFF6B6B76),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color(0xFFF2F2F5),
    outline = Color(0xFFD9D9DE),
    outlineVariant = Color(0xFFE6E6EB)
)

private data class TabItem(val screen: Screen, val label: String, val icon: DrawableResource)

private val TABS = listOf(
    TabItem(Screen.Home, "Inicio", Res.drawable.ic_home),
    TabItem(Screen.Sessions, "Sesiones", Res.drawable.ic_schedule),
    TabItem(Screen.Goals, "Metas", Res.drawable.ic_flag),
    TabItem(Screen.Tutor, "Asistente", Res.drawable.ic_chat),
    TabItem(Screen.Summary, "Resumir", Res.drawable.ic_notes)
)

@OptIn(ExperimentalComposeUiApi::class)
@Suppress("DEPRECATION")
@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = AppColors) {
        var screen by remember { mutableStateOf<Screen>(Screen.Home) }

        // El botón Atrás del celular vuelve a Inicio en lugar de cerrar la app
        BackHandler(enabled = screen != Screen.Home) {
            screen = Screen.Home
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                FloatingNavBar(selected = screen, onSelect = { screen = it })
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    // El contenido empieza debajo de la hora/batería y no se mete detrás al hacer scroll
                    .statusBarsPadding()
            ) {
                when (screen) {
                    Screen.Home -> DashboardScreen(
                        onGoToSessions = { screen = Screen.Sessions },
                        onGoToGoals = { screen = Screen.Goals }
                    )
                    Screen.Sessions -> SessionsScreen()
                    Screen.Goals -> GoalsScreen()
                    Screen.Tutor -> ChatScreen()
                    Screen.Summary -> SummaryScreen()
                }
            }
        }
    }
}

// Barra de navegación flotante: icono arriba y nombre debajo; la pestaña activa se marca en rojo
@Composable
private fun FloatingNavBar(selected: Screen, onSelect: (Screen) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp), clip = false)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TABS.forEach { tab ->
                val isSelected = tab.screen == selected
                val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(tab.screen) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(tab.icon),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = tab.label,
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
