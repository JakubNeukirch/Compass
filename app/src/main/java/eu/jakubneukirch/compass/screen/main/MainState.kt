package eu.jakubneukirch.compass.screen.main

sealed class MainState {
    abstract val degrees: Float

    data class NorthDirectionState(override val degrees: Float) : MainState()
    data class CordsDirectionState(override val degrees: Float) : MainState()
}