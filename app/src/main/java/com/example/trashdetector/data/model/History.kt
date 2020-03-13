package com.example.trashdetector.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    var hisId: Int = 0,
    @ColumnInfo(name = "type")
    var type: String = "",
    @ColumnInfo(name = "time")
    var time: String = "",
    @ColumnInfo(name = "image")
    var image: String = ""
)
