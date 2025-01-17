package com.sarath.gem.core.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Collects values from a [Flow] emitting UI events ([UIEvent]) and delivers them to the [collector] function. The
 * collection is automatically paused and resumed according to the given [lifeCycleState] of the current
 * [LifecycleOwner]. This ensures that events are only processed when the UI is in an appropriate state.
 *
 * This function is designed to handle single events, meaning that each emitted event is processed only once during the
 * active lifecycle state. It leverages `repeatOnLifecycle` to achieve this behavior, automatically restarting the
 * collection whenever the lifecycle enters the specified state and cancelling it when it leaves. This ensures that
 * events are not processed while the UI is in the background or otherwise inactive.
 *
 * **Usage Example:**
 *
 * ```kotlin
 * SingleEventEffect(viewModel.someUiEventFlow) { event ->
 *     // Handle the UI event here
 *     when(event) {
 *      is SomeUIEvent.SomeEvent -> //DO something
 *      is SomeUIEvent.AnotherEvent -> //DO something else
 *      // Handle other UI events
 *     }
 * }
 * ```
 *
 * @param T The type of UI event being collected. Must extend [UIEvent].
 * @param sideEffectFlow The [Flow] emitting the UI events.
 * @param lifeCycleState The [Lifecycle.State] in which the events should be collected. Defaults to
 *   [Lifecycle.State.STARTED].
 * @param collector A function that will be invoked with each collected UI event.
 */
@Composable
fun <T : UIEvent> SingleEventEffect(
    sideEffectFlow: Flow<T>,
    lifeCycleState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(sideEffectFlow) {
        lifecycleOwner.repeatOnLifecycle(lifeCycleState) { sideEffectFlow.collect(collector) }
    }
}
