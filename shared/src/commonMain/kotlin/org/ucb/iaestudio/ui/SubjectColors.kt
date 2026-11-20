package org.ucb.iaestudio.ui

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

private val SUBJECT_PALETTE = listOf(
    Color(0xFFE53935),
    Color(0xFF3B82F6),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFF8B5CF6),
    Color(0xFF14B8A6)
)

// Cada materia recibe siempre el mismo color según su nombre
fun subjectColor(subject: String): Color = SUBJECT_PALETTE[abs(subject.hashCode()) % SUBJECT_PALETTE.size]
