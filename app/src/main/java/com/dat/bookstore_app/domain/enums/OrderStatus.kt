package com.dat.bookstore_app.domain.enums

import com.squareup.moshi.Json

enum class OrderStatus(val title: String) {
    ALL("Tất cả"),
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    SHIPPING("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),

    @Json(name = "CANCELLED")
    CANCELLED("Đã huỷ");// Giữ tên enum là CANCELLED cho đúng với backend

    companion object {
        fun getOrderStatusTitle(status: OrderStatus): String {
            return when (status) {
                PENDING -> "Đơn hàng đang chờ xác nhận"
                CONFIRMED -> "Đơn hàng đã được xác nhận"
                SHIPPING -> "Đơn hàng đang được vận chuyển"
                DELIVERED -> "Đơn hàng đã giao thành công"
                CANCELLED -> "Đơn hàng đã bị huỷ"
                else -> ""
            }
        }

        fun getOrderStatusDescription(status: OrderStatus): String {
            return when (status) {
                SHIPPING -> "Đơn hàng đang được vận chuyển trong vòng 48h"
                PENDING -> "Vui lòng chờ xác nhận đơn hàng từ cửa hàng"
                CONFIRMED -> "Đơn hàng của bạn đang được chuẩn bị giao"
                DELIVERED -> "Cảm ơn bạn đã mua hàng, đơn hàng đã giao"
                CANCELLED -> "Đơn hàng của bạn đã bị hủy!"
                else -> ""
            }
        }
    }
}