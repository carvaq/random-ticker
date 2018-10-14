package com.fanstaticapps.randomticker.mvp

import android.annotation.SuppressLint
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.TickerDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class MainPresenter(private val database: TickerDatabase, private val view: MainView) {

    private lateinit var currentTicker: Bookmark
    private lateinit var bookmarks: List<Bookmark>

    private val randomGenerator = Random(System.currentTimeMillis())

    fun loadDataFromDatabase(currentBookmarkId: Long): Disposable {
        return Single.fromCallable { database.tickerDataDao().getAll() }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    bookmarks = it
                    applyBookmarks(currentBookmarkId)
                }, {
                    bookmarks = listOf()
                    applyBookmarks(currentBookmarkId)
                })

    }

    private fun applyBookmarks(currentBookmarkId: Long) {
        currentTicker = getTickerData(currentBookmarkId)
        view.initializeListeners()
        view.initializeBookmarks()
        currentTicker.run {
            view.applyTickerData(minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds, false, currentTicker.name)
        }
    }

    fun selectBookmark(bookmark: Bookmark?) {
        currentTicker = getTickerData(bookmark?.id)
        currentTicker.run {
            view.applyTickerData(minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds, true, currentTicker.name)
        }
    }

    fun createTimer(name: String, minMin: Int, minSec: Int, maxMin: Int, maxSec: Int) {
        val min = getTotalValueInMillis(minMin, minSec)
        val max = getTotalValueInMillis(maxMin, maxSec)
        if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            val intervalFinished = System.currentTimeMillis() + interval
            saveToPreferences(interval, intervalFinished)
            insertCurrentTickerData(name, minMin, minSec, maxMin, maxSec)

            view.createAlarm()
        } else {
            view.showMinimumMustBeBiggerThanMaximum()
        }
    }

    private fun getTickerData(currentBookmarkId: Long?): Bookmark {
        val data = bookmarks.firstOrNull { it.id == currentBookmarkId }
        return if (data != null) {
            data
        } else {
            val dummy = Bookmark("Random Ticker")
            bookmarks = bookmarks.plus(dummy)
            dummy
        }
    }


    private fun getTotalValueInMillis(minutes: Int, seconds: Int): Int {
        return (60 * minutes + seconds) * 1000
    }

    private fun saveToPreferences(interval: Long, intervalFinished: Long) {
        PREFS.currentlyTickerRunning = true
        PREFS.interval = interval
        PREFS.intervalFinished = intervalFinished
    }

    @SuppressLint("CheckResult")
    private fun insertCurrentTickerData(name: String, minMin: Int, minSec: Int, maxMin: Int, maxSec: Int) {
        currentTicker.name = name
        currentTicker.maximumMinutes = maxMin
        currentTicker.minimumMinutes = minMin
        currentTicker.maximumSeconds = maxSec
        currentTicker.minimumSeconds = minSec
        Single.fromCallable { database.tickerDataDao().insert(currentTicker) }
                .subscribeOn(Schedulers.computation())
                .doOnSuccess {
                    val id = if (currentTicker.id != null) {
                        currentTicker.id!!
                    } else {
                        database.tickerDataDao().getAll().last().id!!
                    }
                    PREFS.currentSelectedId = id
                }
                .subscribe({ _ ->
                    Timber.d("Inserted interval changes")
                }, { throwable -> Timber.e(throwable, "Ooops")})
    }

}