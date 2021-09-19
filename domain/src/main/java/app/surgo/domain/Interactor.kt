package app.surgo.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

sealed class InteractorStatus {
    object Loading : InteractorStatus()
    object Success : InteractorStatus()

    data class Error(val throwable: Throwable) : InteractorStatus()
}

suspend fun Flow<InteractorStatus>.collectWithStatus(
    loadingState: ObservableLoadingCounter
) = collect { status ->
    when (status) {
        InteractorStatus.Loading -> { loadingState.addLoader() }
        InteractorStatus.Success -> { loadingState.removeLoader() }
        is InteractorStatus.Error -> { loadingState.removeLoader() }
    }
}

abstract class Interactor<in P> {
    operator fun invoke(
        params: P,
        timeoutMs: Long = DEFAULT_TIMEOUT_MS
    ): Flow<InteractorStatus> {
        return flow {
            withTimeout(timeoutMs) {
                emit(InteractorStatus.Loading)
                doWork(params)
                emit(InteractorStatus.Success)
            }
        }.catch { t ->
            emit(InteractorStatus.Error(t))
        }
    }

    suspend fun executeSync(params: P) = doWork(params)

    protected abstract suspend fun doWork(params: P)

    companion object {
        private val DEFAULT_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5)
    }
}

abstract class ResultInteractor<in P, R> {
    suspend fun executeSync(params: P): R = doWork(params)

    protected abstract suspend fun doWork(params: P): R

    operator fun invoke(params: P): Flow<R> = flow {
        emit(doWork(params))
    }
}

abstract class PagingInteractor<P : PagingInteractor.Parameters<T>, T : Any> : SubjectInteractor<P, PagingData<T>>() {
    interface Parameters<T : Any> {
        val pagingConfig: PagingConfig
    }
}

abstract class SubjectInteractor<P : Any, T> {
    private val state = MutableSharedFlow<P>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    operator fun invoke(params: P) {
        state.tryEmit(params)
    }

    protected abstract fun createObservable(params: P): Flow<T>

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(): Flow<T> = state.flatMapLatest { createObservable(it) }
}

abstract class SuspendingWorkInteractor<P : Any, T> : SubjectInteractor<P, T>() {
    override fun createObservable(params: P): Flow<T> = flow {
        emit(doWork(params))
    }

    abstract suspend fun doWork(params: P): T
}
