package com.fanstaticapps.randomticker.ui.klaxon

import com.fanstaticapps.randomticker.data.Bookmark

sealed class KlaxonViewState {
    data class ElapseTimeUpdate(val elapsedTime: String?) : KlaxonViewState()
    data class TimerFinished(val elapsedTime: String, val bookmark: Bookmark) : KlaxonViewState()
    object TimerStarted : KlaxonViewState()
    object TimerCanceled : KlaxonViewState()
    object TimerStopped : KlaxonViewState()
}

