package app.camp.gladiator.ui.welcome

import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.viewmodel.*
import app.camp.gladiator.viewmodel.usecase.DelayedCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class WelcomeScreenViewModel(
    delayedCallback: DelayedCallback,
    val delayInMillis:Long
) : BaseViewModel<WelcomeScreenViewModel.Events, WelcomeScreenViewModel.WelcomeScreenState>() {

    sealed class Events : Event {
        object Init : Events()
    }

    sealed class RequiredActions {
        object ProceedForward: RequiredActions()
    }

    data class WelcomeScreenState(
        val requiredActions: RequiredActions? = null
    ) : State

    override fun makeInitState(): WelcomeScreenState = WelcomeScreenState()

    override val useCases: List<UseCase> = listOf(delayedCallback)

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.Init -> emit(DelayedCallback.DelayFor(delayInMillis))
            }.exhaustive
        }
    }

    override fun WelcomeScreenState.plus(result: Result): WelcomeScreenState = when (result) {
        is DelayedCallback.DelayCompletedResult -> copy(requiredActions=RequiredActions.ProceedForward)
        else -> this
    }
}
