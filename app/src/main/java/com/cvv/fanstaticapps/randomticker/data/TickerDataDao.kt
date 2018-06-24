package com.cvv.fanstaticapps.randomticker.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface TickerDataDao {

    @Query("SELECT * from tickerData")
    fun getAll(): Single<List<TickerData>>

    @Insert(onConflict = REPLACE)
    fun insert(tickerData: TickerData)

    @Query("DELETE from tickerData")
    fun deleteAll()

    @Query("SELECT * from tickerData WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<TickerData>

}