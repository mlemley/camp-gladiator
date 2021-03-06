package app.camp.gladiator.viewmodel.usecase

import org.junit.Test

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class DelayedCallbackTest {

    private fun createUseCase():DelayedCallback = DelayedCallback()
    @Test
    fun handles_action() {
        assertThat(createUseCase().canProcess(DelayedCallback.DelayFor(1_000))).isTrue()
    }

    @Test
    fun handles_action__delay_result_passed_back() {
        var actualResult: DelayedCallback.DelayCompletedResult? = null
        runBlocking {
            createUseCase().handleAction(DelayedCallback.DelayFor(1_000)).collect { result ->
                actualResult = result as DelayedCallback.DelayCompletedResult
            }
        }

        assertThat(actualResult).isEqualTo(DelayedCallback.DelayCompletedResult)
    }

}