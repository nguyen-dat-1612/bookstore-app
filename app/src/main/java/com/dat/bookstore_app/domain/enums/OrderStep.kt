package com.dat.bookstore_app.domain.enums

enum class OrderStep(val title: String) {
    NEW("Đơn hàng mới"),
    CONFIRMED("Đã xác nhận"),
    SHIPPING("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELED("Đã huỷ")
}