package com.fanstaticapps.randomticker.mvp

interface MainView {
    fun initializeListeners()
    fun initializeBookmarks()
    fun applyTickerData(minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int, forceDefaultValue: Boolean, name: String)
    fun showMinimumMustBeBiggerThanMaximum()
    fun createAlarm()
}