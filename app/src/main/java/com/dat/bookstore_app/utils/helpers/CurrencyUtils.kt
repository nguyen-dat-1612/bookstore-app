package com.dat.bookstore_app.utils.helpers

import java.text.NumberFormat
import java.util.Locale


object CurrencyUtils {
    fun formatVND(amount: Int): String {
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return format.format(amount) + " ₫"
    }

    fun formatVND(amount: Double): String {
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return format.format(amount) + " ₫"
    }
}