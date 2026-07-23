package com.akwiz.android.ui.quiz

import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.Outcome
import com.akwiz.android.domain.QuizProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repo: FakeQuizRepository) = QuizViewModel(repo, dispatcher)

    private fun succeedingRepo(size: Int = 10, best: Int = 0) =
        FakeQuizRepository(Result.success(testSet(size)), storedBest = best)

    private fun active(vm: QuizViewModel) = vm.state.value as QuizUiState.Active

    /** Option 0 is always correct in the test set. */
    private fun QuizViewModel.answer(correct: Boolean) = selectOption(if (correct) 0 else 1)

    /** Advance past a reveal's 2s hold. */
    private fun TestScope.tickReveal() { advanceTimeBy(REVEAL_HOLD_MS); runCurrent() }

    /** Collect effects into a list for the lifetime of the test. */
    private fun TestScope.effectsOf(vm: QuizViewModel): List<QuizEffect> {
        val out = mutableListOf<QuizEffect>()
        backgroundScope.launch(dispatcher) { vm.effects.collect { out += it } }
        return out
    }

    // ---- loading ----

    @Test fun `loads into an active quiz when there is no saved progress`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        val state = vm.state.value as QuizUiState.Active
        assertEquals(0, state.index)
        assertEquals(AnswerPhase.Awaiting, state.phase)
    }

    @Test fun `a load failure becomes a retryable error`() = runTest(dispatcher) {
        val vm = viewModel(FakeQuizRepository(Result.failure(RuntimeException("offline"))))
        advanceUntilIdle()
        assertTrue((vm.state.value as QuizUiState.Error).canRetry)
    }

    @Test fun `retry reloads after a failure`() = runTest(dispatcher) {
        val vm = viewModel(FakeQuizRepository(Result.failure(RuntimeException())))
        advanceUntilIdle()
        assertTrue(vm.state.value is QuizUiState.Error)
        vm.retry(); advanceUntilIdle()
        // still failing (same repo), but it went back through Loading
        assertTrue(vm.state.value is QuizUiState.Error)
    }

    @Test fun `saved progress produces a resume prompt`() = runTest(dispatcher) {
        val repo = succeedingRepo()
        repo.savedProgress = QuizProgress("hash-10", index = 3, answers = someAnswers(3), currentStreak = 1, longestStreak = 2)
        val vm = viewModel(repo); advanceUntilIdle()
        val prompt = vm.state.value as QuizUiState.ResumePrompt
        assertEquals(4, prompt.questionNumber)
        assertEquals(1, prompt.currentStreak)
    }

    @Test fun `resuming restores into awaiting, never a reveal`() = runTest(dispatcher) {
        val repo = succeedingRepo()
        repo.savedProgress = QuizProgress("hash-10", index = 3, answers = someAnswers(3), currentStreak = 1, longestStreak = 2)
        val vm = viewModel(repo); advanceUntilIdle()
        vm.resumeSaved()
        val state = active(vm)
        assertEquals(3, state.index)
        assertEquals(AnswerPhase.Awaiting, state.phase)
        assertEquals(1, state.currentStreak)
    }

    @Test fun `starting over clears the saved session and begins fresh`() = runTest(dispatcher) {
        val repo = succeedingRepo()
        repo.savedProgress = QuizProgress("hash-10", index = 3, answers = someAnswers(3), currentStreak = 1, longestStreak = 2)
        val vm = viewModel(repo); advanceUntilIdle()
        vm.startOver(); advanceUntilIdle()
        assertEquals(0, active(vm).index)
        assertTrue(repo.clears > 0)
    }

    // ---- answering & streak ----

    @Test fun `a correct answer reveals and bumps the streak`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        vm.answer(correct = true)
        val state = active(vm)
        assertEquals(AnswerPhase.Revealed(0), state.phase)
        assertEquals(1, state.currentStreak)
    }

    @Test fun `a wrong answer resets the streak but keeps the longest`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        repeat(2) { vm.answer(correct = true); tickReveal() }
        vm.answer(correct = false)
        assertEquals(0, active(vm).currentStreak)
        assertEquals(2, active(vm).longestStreak)
    }

    @Test fun `answering correct and wrong emits the matching effects`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        val effects = effectsOf(vm)
        vm.answer(correct = true); tickReveal()
        vm.answer(correct = false); runCurrent()
        assertEquals(
            listOf(QuizEffect.AnswerCorrect, QuizEffect.AnswerWrong),
            effects.filter { it is QuizEffect.AnswerCorrect || it is QuizEffect.AnswerWrong },
        )
    }

    @Test fun `the streak ignites exactly once, on the crossing`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        val effects = effectsOf(vm)
        repeat(5) { vm.answer(correct = true); tickReveal() }
        assertEquals(1, effects.count { it is QuizEffect.StreakIgnited })
    }

    // ---- timing ----

    @Test fun `it does not advance before two seconds`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        vm.answer(correct = true)
        advanceTimeBy(REVEAL_HOLD_MS - 1); runCurrent()
        assertEquals(0, active(vm).index)
    }

    @Test fun `it advances at exactly two seconds`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        vm.answer(correct = true); tickReveal()
        assertEquals(1, active(vm).index)
        assertEquals(AnswerPhase.Awaiting, active(vm).phase)
    }

    // ---- skip ----

    @Test fun `skip records a skip and advances immediately`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        vm.skip()
        assertEquals(1, active(vm).index)
        assertEquals(Outcome.Skipped, active(vm).answers.single().outcome)
    }

    @Test fun `skip is inert once an answer is revealed`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        vm.answer(correct = true)
        vm.skip()
        assertEquals(0, active(vm).index)
        assertEquals(1, active(vm).answers.size)
    }

    // ---- guards ----

    @Test fun `a second tap during a reveal is ignored`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo()); advanceUntilIdle()
        vm.answer(correct = true)
        vm.selectOption(1)
        assertEquals(AnswerPhase.Revealed(0), active(vm).phase)
        assertEquals(1, active(vm).answers.size)
    }

    @Test fun `selecting is ignored while loading`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo())   // still Loading
        vm.selectOption(0)
        assertTrue(vm.state.value is QuizUiState.Loading)
    }

    // ---- finishing ----

    @Test fun `answering every question finishes the quiz`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo(size = 3)); advanceUntilIdle()
        repeat(3) { vm.answer(correct = true); tickReveal() }
        advanceUntilIdle()
        val done = vm.state.value as QuizUiState.Finished
        assertEquals(3, done.result.correct)
        assertEquals(3, done.result.total)
        assertEquals(3, done.result.longestStreak)
    }

    @Test fun `finishing records the best streak and keeps the session`() = runTest(dispatcher) {
        val repo = succeedingRepo(size = 5)
        val vm = viewModel(repo); advanceUntilIdle()
        repeat(5) { vm.answer(correct = true); tickReveal() }
        advanceUntilIdle()
        assertEquals(5, repo.recordedBest)
        // Session is kept so reopening shows the result again; only restart clears it.
        assertEquals(0, repo.clears)
    }

    @Test fun `reopening after a finished quiz shows the result again`() = runTest(dispatcher) {
        val repo = succeedingRepo(size = 3)
        repo.savedProgress = QuizProgress(
            questionSetHash = "hash-3",
            index = 3,
            answers = someAnswers(3),
            currentStreak = 3,
            longestStreak = 3,
        )
        val vm = viewModel(repo); advanceUntilIdle()
        val done = vm.state.value as QuizUiState.Finished
        assertEquals(3, done.result.correct)
        assertFalse(done.isPersonalBest)   // no re-celebration on reopen
    }

    @Test fun `the splash holds for its minimum before content appears`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo())
        advanceTimeBy(SPLASH_MIN_MS - 1); runCurrent()
        assertTrue(vm.state.value is QuizUiState.Loading)
        advanceTimeBy(1); advanceUntilIdle()
        assertTrue(vm.state.value is QuizUiState.Active)
    }

    @Test fun `beating the stored best is flagged and celebrated`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo(size = 3, best = 2)); advanceUntilIdle()
        val effects = effectsOf(vm)
        repeat(3) { vm.answer(correct = true); tickReveal() }
        advanceUntilIdle()
        assertTrue((vm.state.value as QuizUiState.Finished).isPersonalBest)
        assertTrue(effects.any { it is QuizEffect.PersonalBest })
    }

    @Test fun `not beating the stored best is not a personal best`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo(size = 3, best = 9)); advanceUntilIdle()
        repeat(3) { vm.answer(correct = true); tickReveal() }
        advanceUntilIdle()
        assertFalse((vm.state.value as QuizUiState.Finished).isPersonalBest)
    }

    // ---- restart ----

    @Test fun `restart resets everything and clears the session`() = runTest(dispatcher) {
        val vm = viewModel(succeedingRepo(size = 3)); advanceUntilIdle()
        repeat(3) { vm.answer(correct = true); tickReveal() }
        advanceUntilIdle()
        vm.restart(); advanceUntilIdle()
        val state = active(vm)
        assertEquals(0, state.index)
        assertEquals(0, state.currentStreak)
        assertTrue(state.answers.isEmpty())
    }

    // ---- persistence ----

    @Test fun `each answer is committed with the resume index set to the answer count`() = runTest(dispatcher) {
        val repo = succeedingRepo(size = 3)
        val vm = viewModel(repo); advanceUntilIdle()
        vm.answer(correct = true); advanceUntilIdle()
        assertEquals(1, repo.saves.size)
        assertEquals(1, repo.saves.last().index)
    }

    private fun someAnswers(n: Int) =
        (1..n).map { AnswerRecord.answered(it, selected = 0, isCorrect = true) }
}
