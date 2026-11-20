package org.ucb.iaestudio.study.domain.usecase

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.repository.StudyRepository

class AddSessionUseCase(
    private val repository: StudyRepository
) {
    // Valida los datos, normaliza la materia y guarda la sesión con la fecha actual
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(subject: String, minutes: Int): Result<Unit> {
        val cleanSubject = subject.trim().replaceFirstChar { it.uppercase() }
        if (cleanSubject.isBlank()) return Result.failure(Exception("Escribe la materia"))
        if (minutes <= 0) return Result.failure(Exception("Elige cuánto tiempo estudiaste"))

        val now = Clock.System.now().toEpochMilliseconds()
        // id = 0: Room genera el id automáticamente
        repository.addSession(StudySession(id = 0, subject = cleanSubject, minutes = minutes, dateMillis = now))
        return Result.success(Unit)
    }
}
