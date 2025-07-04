package com.plus.baseandroidapp.utils.helpers

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    private fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    }

    @JvmStatic
    fun fromDate(date: Date?): String? {
        return date?.let { getDateFormat().format(it) }
    }

    @JvmStatic
    fun toDate(dateString: String?): Date? {
        return dateString?.let { getDateFormat().parse(it) }
    }

    @TypeConverter
    @JvmStatic
    fun dateToString(date: Date?): String? {
        return fromDate(date)
    }

    @TypeConverter
    @JvmStatic
    fun stringToDate(dateString: String?): Date? {
        return toDate(dateString)
    }
}