package app.camp.gladiator.ui.welcome

import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.UseCase
import app.camp.gladiator.viewmodel.usecase.DelayedCallback
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class WelcomeScreenViewModelTest {

    val delayInMillis: Long = 0

    private fun createViewModel(
        delayedCallback: DelayedCallback = mockk(relaxUnitFun = true)
    ): WelcomeScreenViewModel = WelcomeScreenViewModel(
        delayedCallback,
        delayInMillis
    )

    @Test
    fun event_transform_maps_correctly() {
        val viewModel = createViewModel()
        val events = flowOf(
            WelcomeScreenViewModel.Events.Init
        )

        val actual = mutableListOf<Action>()
        runBlocking {
            with(viewModel) {
                events.eventTransform().toList(actual)
            }
        }

        val expected = listOf(
            DelayedCallback.DelayFor(delayInMillis)
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun contains_expected_use_cases() {
        val delayedCallback = DelayedCallback()
        val expected = listOf<UseCase>(delayedCallback)

        assertThat(createViewModel(delayedCallback = delayedCallback).useCases).isEqualTo(expected)
    }

    @Test
    fun plus__delay_result__instructs_proceed_forward() {
        val viewModel = createViewModel()
        val initState = viewModel.makeInitState()

        val inputs = listOf(
            DelayedCallback.DelayCompletedResult
        )
        val expected = listOf(
            initState.copy(requiredActions = WelcomeScreenViewModel.RequiredActions.ProceedForward)
        )
        with(viewModel) {
            val actual = inputs.map { initState + it }
            assertThat(expected).isEqualTo(actual)
        }
    }
}