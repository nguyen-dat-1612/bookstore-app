package com.dat.bookstore_app.domain.enums

enum class TransactionStatus {
    PENDING, // Giao dịch đang chờ xử lý
    SUCCESS, // Giao dịch thành công
    FAILED,  // Giao dịch thất bại
    REFUNDED, // Giao dịch đã hoàn tiền
    CANCELLED // Giao dịch bị hủy
}