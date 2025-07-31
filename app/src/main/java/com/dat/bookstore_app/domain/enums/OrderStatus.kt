package com.dat.bookstore_app.domain.enums

import com.squareup.moshi.Json

enum class OrderStatus(val title: String) {
    ALL("Tất cả"),
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    SHIPPING("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),

    @Json(name = "CANCELLED")
    CANCELLED("Đã huỷ") // Giữ tên enum là CANCELLED cho đúng với backend
}