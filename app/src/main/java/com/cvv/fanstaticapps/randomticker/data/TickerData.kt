package com.cvv.fanstaticapps.randomticker.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by carla on 6/24/18
 */

@Entity(tableName = "tickerData")
data class TickerData(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "minimumMinutes") var minimumMinutes: Int,
                      @ColumnInfo(name = "minimumSeconds") var minimumSeconds: Int,
                      @ColumnInfo(name = "maximumMinutes") var maximumMinutes: Int,
                      @ColumnInfo(name = "maximumSeconds") var maximumSeconds: Int) {
    constructor() : this(null, 0, 0, 5, 0)
}







