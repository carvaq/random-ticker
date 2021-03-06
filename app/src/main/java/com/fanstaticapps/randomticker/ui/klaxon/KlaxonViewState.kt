package com.fanstaticapps.randomticker.ui.klaxon

import com.fanstaticapps.randomticker.data.Bookmark

sealed class KlaxonViewState {
    data class ElapsedTimeUpdate(val elapsedTime: String?) : KlaxonViewState()
    data class TickerFinished(val elapsedTime: String, val bookmark: Bookmark) : KlaxonViewState()
    data class TickerStarted(val bookmark: Bookmark) : KlaxonViewState()
    object TickerCanceled : KlaxonViewState()
    object TickerStopped : KlaxonViewState()
    object TickerNoop : KlaxonViewState()
}

