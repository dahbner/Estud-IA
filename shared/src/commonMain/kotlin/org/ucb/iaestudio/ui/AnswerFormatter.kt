package org.ucb.iaestudio.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

private val HEADING_REGEX = Regex("""^#{1,6}\s+""")
private val BULLET_REGEX = Regex("""^(\s*)[*-]\s+""")
private val ITALIC_REGEX = Regex("""\*(\S(?:.*?\S)?)\*""")

// Gemini responde en Markdown: convierte **texto** en negrita y quita los demás símbolos
fun formatAnswer(text: String): AnnotatedString = buildAnnotatedString {
    text.trim().lines().forEachIndexed { lineIndex, rawLine ->
        if (lineIndex > 0) append("\n")

        val isHeading = HEADING_REGEX.containsMatchIn(rawLine)
        val line = rawLine.replace(HEADING_REGEX, "").replace(BULLET_REGEX, "$1• ")
        val parts = line.split("**")

        parts.forEachIndexed { partIndex, part ->
            val cleanPart = part.replace(ITALIC_REGEX, "$1")
            // Las partes impares están entre ** y **; la última impar quedó sin cerrar
            val isBold = isHeading || (partIndex % 2 == 1 && partIndex < parts.lastIndex)
            if (isBold) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(cleanPart) }
            } else {
                append(cleanPart)
            }
        }
    }
}
