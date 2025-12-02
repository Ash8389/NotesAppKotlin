package com.example.notes.converter

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun timeStampFromDate(date: Date) : Long {
        return date.time
    }
    @TypeConverter
    fun dateFromTimeStamp(timeStamp: Long) : Date? {
        return Date(timeStamp)
    }
}