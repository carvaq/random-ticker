package com.fanstaticapps.randomticker.ui.klaxon

sealed class KlaxonViewState {
    data class ElapseTimeUpdate(val elapsedTime: String?) : KlaxonViewState()
    data class TimerFinished(val elapsedTime: String) : KlaxonViewState()
    object TimerStarted : KlaxonViewState()
    object TimerCanceled : KlaxonViewState()
    object TimerStopped : KlaxonViewState()
}