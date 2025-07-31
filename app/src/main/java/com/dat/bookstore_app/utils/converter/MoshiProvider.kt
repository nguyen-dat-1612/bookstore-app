package com.dat.bookstore_app.utils.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

object MoshiProvider {
    val moshi: Moshi by lazy {
        Moshi.Builder().build()
    }

    inline fun <reified T> adapter(): JsonAdapter<T> =
        moshi.adapter(T::class.java)
}
