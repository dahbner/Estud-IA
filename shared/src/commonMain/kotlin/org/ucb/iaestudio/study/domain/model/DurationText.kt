package org.ucb.iaestudio.study.domain.model

// 45 -> "45 min", 60 -> "1 h", 90 -> "1 h 30 min"
fun Int.toDurationText(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours == 0 -> "$minutes min"
        minutes == 0 -> "$hours h"
        else -> "$hours h $minutes min"
    }
}
