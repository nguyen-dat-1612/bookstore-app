package com.plus.baseandroidapp.data.datasource.datastore

import android.util.Base64

class EncryptionManager {
    fun encrypt(data: String): String {
        return Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String?): String {
        return String(Base64.decode(encryptedData, Base64.DEFAULT))
    }
}