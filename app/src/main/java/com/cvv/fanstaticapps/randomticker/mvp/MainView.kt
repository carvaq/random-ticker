package com.cvv.fanstaticapps.randomticker.mvp

interface MainView {
    fun initializeListeners()
    fun initializeBookmarks(bookmarkNames: List<String>, selectedBookmark: Int)
    fun applyTickerData(minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int, forceDefaultValue: Boolean)
    fun showMinimumMustBeBiggerThanMaximum()
    fun createAlarm()
    fun showCreateNewBookmarkDialog()
}