package com.example.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface IViewModel<Event, State, Effect> {

    val state: StateFlow<State>
    val event: SharedFlow<Event>
    val effect: Flow<Effect>

    fun setEvent(event: Event)
}

abstract class ViewModel<Event : UIEvent, State : UIState, Effect : UIEffect> :
    IViewModel<Event, State, Effect>, ViewModel() {

    val tag: String
        get() = this::class.simpleName ?: "UnnamedClass"

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    val currentState: State
        get() = _state.value

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    override val event = _event.asSharedFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    override val state = _state.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    override val effect = _effect.receiveAsFlow()

    init {

        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    abstract fun handleEvent(event: Event)

    override fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _state.value = newState
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    protected fun <T> collect(
        flow: Flow<T>, collect: (T) -> Unit
    ) {
        viewModelScope.launch {
            flow.collect {
                collect(it)
            }
        }
    }
}