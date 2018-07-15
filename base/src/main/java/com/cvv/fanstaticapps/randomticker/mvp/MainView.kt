package com.cvv.fanstaticapps.randomticker.mvp

interface MainView {
    fun initializeListeners()
    fun initializeBookmarks(initialSelectedBookmark: Int)
    fun applyTickerData(minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int, forceDefaultValue: Boolean, selectedPosition: Int)
    fun showMinimumMustBeBiggerThanMaximum()
    fun createAlarm()
}