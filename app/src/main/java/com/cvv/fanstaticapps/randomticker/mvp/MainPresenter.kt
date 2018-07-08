package com.cvv.fanstaticapps.randomticker.mvp

import android.util.Log
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.data.TickerData
import com.cvv.fanstaticapps.randomticker.data.TickerDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainPresenter(private val database: TickerDatabase, private val view: MainView) {
    private lateinit var currentTicker: TickerData
    private lateinit var bookmarks: List<TickerData>
    private lateinit var bookmarkNames: List<String>

    private val randomGenerator = Random(System.currentTimeMillis())

    fun loadDataFromDatabase(currentIndex: Int = 0) {
        database.tickerDataDao().getAll()
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
        bookmarkNames = fetchBookmarkNames()
        prepareInitialView(currentIndex)
    }

    fun selectBookmark(index: Int) {
        when {
            index < bookmarks.size -> {
                currentTicker = getTickerData(index)
                currentTicker.run {
                    view.applyTickerData(minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds, true)
                }
            }
            else -> {
                view.showCreateNewBookmarkDialog()
            }
        }
    }

    fun deleteBookmark(position: Int): Boolean {
        return when {
            position < bookmarks.size -> {
                val tickerToBeDeleted = getTickerData(position)
                Single.fromCallable { database.tickerDataDao().delete(tickerToBeDeleted.id) }
                        .subscribeOn(Schedulers.computation())
                        .subscribe { _ -> Log.d("DB", "Bookmark deletion finished successfully") }

                true
            }
            else -> false
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

    fun createBookmark(bookmarkName: String) {
        val newTickerBookmark = TickerData(bookmarkName)
        Single.fromCallable { database.tickerDataDao().insert(newTickerBookmark) }
                .subscribeOn(Schedulers.computation())
                .subscribe { _ ->
                    Log.d("DB", "Inserted interval changes")
                    loadDataFromDatabase(bookmarks.size)
                }
    }


    private fun prepareInitialView(selectedBookmark: Int) {
        view.initializeListeners()
        view.initializeBookmarks(bookmarkNames, selectedBookmark)
        currentTicker.run {
            view.applyTickerData(minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds, false)
        }
    }

    private fun fetchBookmarkNames(): List<String> {
        val bookmarkNames = mutableListOf<String>()
        bookmarks.mapTo(bookmarkNames) { it.bookmarkName }
        return bookmarkNames
    }

    private fun getTickerData(currentSelectedIndex: Int): TickerData {
        return if (currentSelectedIndex < bookmarks.size) {
            bookmarks[currentSelectedIndex]
        } else {
            val dummy = TickerData("Random Ticker")
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
        insertCurrentTickerData()
        PREFS.currentlyTickerRunning = true
        PREFS.interval = interval
        PREFS.intervalFinished = intervalFinished
    }

    private fun insertCurrentTickerData() {
        Single.fromCallable { database.tickerDataDao().insert(currentTicker) }
                .subscribeOn(Schedulers.computation())
                .subscribe { _ -> Log.d("DB", "Inserted interval changes") }
    }

    fun saveBookmark(newName: String, position: Int) {
        if (newName.isBlank()) {
            deleteBookmark(position)
        } else {
            val editedBookmark = getTickerData(position)
            editedBookmark.bookmarkName = newName
        }
    }

}