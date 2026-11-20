package org.ucb.iaestudio.navigation

// Las 5 pestañas de la barra inferior; App.kt muestra una según este valor
sealed interface Screen {
    data object Home : Screen
    data object Sessions : Screen
    data object Goals : Screen
    data object Tutor : Screen
    data object Summary : Screen
}
