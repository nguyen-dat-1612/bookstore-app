package com.dat.bookstore_app.utils.helpers

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateHelper {

    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    private fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        }
    }

    @JvmStatic
    fun fromDate(date: Date?): String? {
        return date?.let { getDateFormat().format(it) }
    }

    @JvmStatic
    fun toDate(dateString: String?): Date? {
        return dateString?.let { getDateFormat().parse(it) }
    }

    fun formatOrderDate(rawDate: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            LocalDateTime.parse(rawDate, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            rawDate // fallback nếu sai format
        }
    }
}
