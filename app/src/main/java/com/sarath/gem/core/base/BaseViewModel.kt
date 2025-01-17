package com.sarath.gem.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/** Represents the state of the UI. */
interface UIState

/**
 * An interface representing a UI event. UI events are occurrences that happen in the user interface, such as alert,
 * navigation, toast, etc.
 */
interface UIEvent

/**
 * Represents an action event.
 *
 * Action events are typically used to signal that a user has interacted with a UI element, such as clicking a button or
 * selecting an item from a list.
 */
interface UIAction

/**
 * Base class for ViewModels that follow a Unidirectional Data Flow (UDF) pattern. This class manages the state, events,
 * and actions of the ViewModel.
 *
 * @param State The type of the UI state.
 * @param Event The type of the UI event.
 * @param Action The type of the action event.
 */
abstract class BaseViewModel<State : UIState, Event : UIEvent, Action : UIAction>() : ViewModel() {
    /**
     * The initial state of the [State] object. This is lazily initialized so that it is only created when it is first
     * accessed.
     */
    private val initialState: State by lazy { initialState() }

    /** Returns the initial state of the entity. This function is called only once when the entity is first created. */
    abstract fun initialState(): State

    protected abstract fun onActionEvent(action: Action)

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    private val _uiEventFlow = Channel<Event>(capacity = Channel.BUFFERED)
    val uiEvent = _uiEventFlow.receiveAsFlow()
    /**
     * Gets the current [State] of the UI.
     *
     * @return The current [State] exposed as a read-only property.
     */
    protected val currentState: State
        get() = uiState.value

    /**
     * Updates the UI state using the provided update function.
     *
     * This function applies the given lambda to the current state and updates the internal [_uiState] with the result.
     *
     * @param updatedState A lambda function that takes the current state and returns the updated state.
     */
    protected fun update(updatedState: State.() -> State) = _uiState.update(updatedState)

    /**
     * Sends a one-time UI event to the UI layer.
     *
     * This function launches a coroutine to emit the given event to the `_uiEventFlow`. The event will be consumed by
     * the UI layer and will not be replayed to new subscribers. This is suitable for events that should trigger a
     * single action in the UI, such as displaying a Snackbar, navigating to a screen, or showing a dialog.
     *
     * **Note:** This function uses [SharedFlow] with a `replay` value of 0, ensuring that only the latest event is
     * delivered to active collectors. Events emitted while no collectors are active will be dropped.
     *
     * @param event[UIEvent] The UI event to send.
     */
    @OptIn(InternalCoroutinesApi::class)
    protected fun sendOneTimeUIEvent(event: Event, delayMillis: Long? = null) {
        launch {
            delayMillis?.let { delay(it) }
            _uiEventFlow.send(event)
        }
    }

    /**
     * Handles the given [action].
     *
     * This function simply delegates the handling to [onActionEvent].
     *
     * @param action The action to be handled.
     */
    fun onAction(action: Action) {
        onActionEvent(action)
    }
}
