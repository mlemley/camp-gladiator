package app.camp.gladiator.ui.welcome

import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.viewmodel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class WelcomeScreenViewModel(
    override val useCases: List<UseCase>
) : BaseViewModel<WelcomeScreenViewModel.Events, WelcomeScreenViewModel.WelcomeScreenState>() {

    sealed class Events : Event {
        object Init : Events()
    }

    sealed class RequiredActions {
        object proceedForward
    }

    data class WelcomeScreenState(
        val requiredActions: RequiredActions? = null
    ) : State

    override fun makeInitState(): WelcomeScreenState = WelcomeScreenState()

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.Init -> throw IllegalStateException("TODO IMPLEMENT")
            }.exhaustive
        }
    }

    override fun WelcomeScreenState.plus(result: Result): WelcomeScreenState = when (result) {
        else -> this
    }


}
