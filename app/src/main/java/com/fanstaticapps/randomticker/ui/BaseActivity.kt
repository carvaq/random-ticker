package com.fanstaticapps.randomticker.ui

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

abstract class BaseActivity : AppCompatActivity() {

    private var compositeDisposable: CompositeDisposable? = null

    protected fun toast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    fun addDisposable(disposable: Disposable) {
        getCompositeDisposable().add(disposable)
    }

    override fun onDestroy() {
        getCompositeDisposable().dispose()
        super.onDestroy()
    }

    private fun getCompositeDisposable(): CompositeDisposable {
        if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        return compositeDisposable!!
    }

}
