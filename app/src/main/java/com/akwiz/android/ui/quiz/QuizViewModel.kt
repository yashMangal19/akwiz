package com.akwiz.android.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akwiz.android.data.QuizRepository
import com.akwiz.android.domain.Outcome
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizProgress
import com.akwiz.android.domain.QuizResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

const val REVEAL_HOLD_MS = 2_000L

class QuizViewModel(
    private val repository: QuizRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {

    private val _state = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    private val _effects = Channel<QuizEffect>(Channel.BUFFERED)
    val effects: Flow<QuizEffect> = _effects.receiveAsFlow()

    private var advanceJob: Job? = null

    init { load() }

    fun retry() = load()

    private fun load() {
        _state.value = QuizUiState.Loading
        viewModelScope.launch {
            repository.loadQuestions().fold(
                onSuccess = { set ->
                    val progress = repository.readProgress(set)
                    _state.value = if (progress != null) {
                        QuizUiState.ResumePrompt(set, progress)
                    } else {
                        freshQuiz(set)
                    }
                },
                onFailure = {
                    _state.value = QuizUiState.Error("Couldn't load the quiz.", canRetry = true)
                },
            )
        }
    }

    fun resumeSaved() {
        val prompt = _state.value as? QuizUiState.ResumePrompt ?: return
        _state.value = QuizUiState.Active(
            set = prompt.set,
            index = prompt.progress.index,
            phase = AnswerPhase.Awaiting,   // never restore a reveal — its timer is gone
            answers = prompt.progress.answers,
            currentStreak = prompt.progress.currentStreak,
            longestStreak = prompt.progress.longestStreak,
        )
    }

    fun startOver() {
        val prompt = _state.value as? QuizUiState.ResumePrompt ?: return
        clearSaved()
        _state.value = freshQuiz(prompt.set)
    }

    fun selectOption(index: Int) {
        val active = _state.value as? QuizUiState.Active ?: return
        if (active.isRevealed) return                              // already answered
        if (index !in active.currentQuestion.options.indices) return

        val revealed = active.recordAnswer(index)
        _state.value = revealed

        // Committed on tap, so a kill mid-reveal resumes on the *next* question.
        persist(revealed)

        val correct = revealed.answers.last().outcome == Outcome.Correct
        _effects.trySend(if (correct) QuizEffect.AnswerCorrect else QuizEffect.AnswerWrong)
        if (!active.isStreakHot && revealed.isStreakHot) _effects.trySend(QuizEffect.StreakIgnited)

        advanceJob = viewModelScope.launch(dispatcher) {
            delay(REVEAL_HOLD_MS)
            advance()
        }
    }

    fun skip() {
        val active = _state.value as? QuizUiState.Active ?: return
        if (active.isRevealed) return                              // Skip is inert once answered
        val skipped = active.recordSkip()
        _state.value = skipped
        persist(skipped)
        advance()
    }

    fun restart() {
        val active = _state.value as? QuizUiState.Active
        val finished = _state.value as? QuizUiState.Finished
        val set = active?.set ?: finished?.set ?: return
        advanceJob?.cancel()
        clearSaved()
        _state.value = freshQuiz(set)
    }

    private fun advance() {
        val active = _state.value as? QuizUiState.Active ?: return
        val next = active.advancedOrNull()
        if (next != null) {
            _state.value = next
        } else {
            finish(active)
        }
    }

    private fun finish(active: QuizUiState.Active) {
        val result = QuizResult(active.answers, active.longestStreak)
        viewModelScope.launch {
            val best = repository.bestStreak()
            val isPersonalBest = active.longestStreak > best
            repository.recordBestStreak(active.longestStreak)
            repository.clearProgress()
            _state.value = QuizUiState.Finished(active.set, result, isPersonalBest)
            if (isPersonalBest) _effects.trySend(QuizEffect.PersonalBest)
        }
    }

    private fun freshQuiz(set: QuestionSet) = QuizUiState.Active(
        set = set,
        index = 0,
        phase = AnswerPhase.Awaiting,
        answers = emptyList(),
        currentStreak = 0,
        longestStreak = 0,
    )

    private fun persist(active: QuizUiState.Active) {
        val progress = QuizProgress(
            questionSetHash = active.set.contentHash,
            index = active.answers.size,
            answers = active.answers,
            currentStreak = active.currentStreak,
            longestStreak = active.longestStreak,
        )
        viewModelScope.launch { repository.saveProgress(progress) }
    }

    private fun clearSaved() {
        viewModelScope.launch { repository.clearProgress() }
    }
}
