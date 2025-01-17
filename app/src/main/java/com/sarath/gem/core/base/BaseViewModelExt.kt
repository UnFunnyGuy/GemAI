package com.sarath.gem.core.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collects the UI state from a [BaseViewModel] as a [State] and observes it with Lifecycle awareness.
 *
 * This is a convenience function for easily accessing the UI state exposed by a ViewModel. It uses
 * `collectAsStateWithLifecycle` to ensure that state collection is lifecycle-aware.
 *
 * @param S The type of the UI state.
 * @param VM The type of the ViewModel, which must be a subclass of [BaseViewModel].
 * @return A [State] object that holds the latest UI state.
 */
@Composable fun <S : UIState, VM : BaseViewModel<S, *, *>> VM.collectState() = uiState.collectAsStateWithLifecycle()

/**
 * Collects UI events emitted by the [BaseViewModel] as a [State] with lifecycle awareness.
 *
 * This composable function allows you to observe and react to UI events emitted by the ViewModel. The events are
 * collected as a [State] object, ensuring that collection is automatically paused and resumed according to the
 * lifecycle of the composable.
 *
 * @param E The type of UI event emitted by the ViewModel.
 * @param VM The type of BaseViewModel, which must emit events of type E.
 * @return A [State] object containing the latest UI event emitted by the ViewModel, or null if no event has been
 *   emitted yet.
 */
@Composable fun <E : UIEvent, VM : BaseViewModel<*, E, *>> VM.collectEvent() = uiEvent.collectAsStateWithLifecycle(null)

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job]. The
 * coroutine is launched in the [viewModelScope] of this ViewModel.
 *
 * @param context additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
 * @param operation the coroutine code which will be invoked in the context of the provided scope.
 * @return the job object that can be used to cancel the launched coroutine.
 */
fun BaseViewModel<*, *, *>.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    operation: suspend CoroutineScope.() -> Unit,
): Job {
    return viewModelScope.launch(context = context, start = start, block = operation)
}
