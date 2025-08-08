package com.dat.bookstore_app.domain.enums

import com.squareup.moshi.Json

enum class TransactionStatus(val title: String) {
    PENDING("Đang chờ xử lý"),
    SUCCESS("Thành công"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền"),

    @Json(name = "CANCELLED")
    CANCELLED("Đã huỷ")
}