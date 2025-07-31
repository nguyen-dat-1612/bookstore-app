package com.dat.bookstore_app.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl


class AppCookieJar : CookieJar {
    private val cookieStore = mutableMapOf<String, List<Cookie>>() // dùng host (String) làm key

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host].orEmpty()
    }

    fun getCookie(name: String): String? {
        return cookieStore.values.flatten().firstOrNull { it.name == name }?.value
    }
}
