package com.cvv.fanstaticapps.randomticker.mvp

import android.util.Log
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.data.TickerData
import com.cvv.fanstaticapps.randomticker.data.TickerDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainPresenter(private val database: TickerDatabase, private val view: MainView) {

    private lateinit var currentTicker: TickerData
    private lateinit var bookmarks: List<TickerData>

    private val randomGenerator = Random(System.currentTimeMillis())

    fun loadDataFromDatabase(currentIndex: Int): Disposable {
        return database.tickerDataDao().getAll()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    bookmarks = it
                    applyBookmarks(currentIndex)
                }, {
                    bookmarks = listOf()
                    applyBookmarks(currentIndex)
                })

    }

    private fun applyBookmarks(currentIndex: Int) {
        currentTicker = getTickerData(currentIndex)
        prepareInitialView(currentIndex)
    }

    fun selectBookmark(index: Int) {
        currentTicker = getTickerData(index)
        currentTicker.run {
            view.applyTickerData(minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds, true, index)
        }
    }

    fun createTimer(minMin: Int, minSec: Int, maxMin: Int, maxSec: Int) {
        val min = getTotalValueInMillis(minMin, minSec)
        val max = getTotalValueInMillis(maxMin, maxSec)
        if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            val intervalFinished = System.currentTimeMillis() + interval
            saveToPreferences(minMin, minSec, maxMin, maxSec, interval, intervalFinished)
            view.createAlarm()
        } else {
            view.showMinimumMustBeBiggerThanMaximum()
        }
    }

    private fun prepareInitialView(selectedBookmark: Int) {
        view.initializeListeners()
        view.initializeBookmarks(selectedBookmark)
        currentTicker.run {
            view.applyTickerData(minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds, false, selectedBookmark)
        }
    }

    private fun getTickerData(currentSelectedIndex: Int): TickerData {
        val data = bookmarks.firstOrNull() { it.bookmarkPosition == currentSelectedIndex }
        return if (data != null) {
            data
        } else {
            val dummy = TickerData(currentSelectedIndex)
            bookmarks = bookmarks.plus(dummy)
            dummy
        }
    }


    private fun getTotalValueInMillis(minutes: Int, seconds: Int): Int {
        return (60 * minutes + seconds) * 1000
    }

    private fun saveToPreferences(minMin: Int, minSec: Int, maxMin: Int, maxSec: Int, interval: Long, intervalFinished: Long) {
        currentTicker.maximumMinutes = maxMin
        currentTicker.minimumMinutes = minMin
        currentTicker.maximumSeconds = maxSec
        currentTicker.minimumSeconds = minSec
        PREFS.currentlyTickerRunning = true
        PREFS.interval = interval
        PREFS.intervalFinished = intervalFinished
        PREFS.currentSelectedPosition = currentTicker.bookmarkPosition
        insertCurrentTickerData()
    }

    private fun insertCurrentTickerData() {
        Single.fromCallable { database.tickerDataDao().insert(currentTicker) }
                .subscribeOn(Schedulers.computation())
                .subscribe { _ -> Log.d("DB", "Inserted interval changes") }
    }


}