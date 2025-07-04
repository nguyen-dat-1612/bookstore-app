package com.plus.baseandroidapp.utils.converter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
class DateJsonAdapter {

    @ToJson
    fun toJson(date: Date): String {
        return ISO_8601_FORMAT.format(date)
    }

    @FromJson
    fun fromJson(dateString: String): Date {
        try {
            return ISO_8601_FORMAT.parse(dateString)
                ?: throw IllegalArgumentException("Unable to parse date: $dateString")
        } catch (e: Exception) {
            throw JsonDataException("Date parsing error for: $dateString", e)
        }
    }

    companion object {
        private val ISO_8601_FORMAT =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
    }
}
