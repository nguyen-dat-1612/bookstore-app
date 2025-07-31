package com.dat.bookstore_app.domain.enums

enum class PaymentMethod(
    val paymentTitle: String
) {
    COD("Thanh toán khi nhận hàng"),
    VNPAY("Thanh toán qua VNPAY")
}