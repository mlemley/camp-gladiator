package app.camp.gladiator.viewmodel


import kotlinx.coroutines.flow.Flow

interface UseCase {
    fun canProcess(action: Action): Boolean
    fun handleAction(action: Action): Flow<Result>
}
