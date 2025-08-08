package com.dat.bookstore_app.domain.enums

enum class OrderFlowState {
    ORDER_CREATED_COD,              // Đơn COD đã tạo → chuyển luôn sang trang thành công
    ORDER_CREATED_VNPAY_REDIRECTED, // Đơn VNPAY đã tạo → đang redirect
    ORDER_VNPAY_SUCCESS,            // VNPAY thanh toán thành công
    ORDER_VNPAY_PENDING,            // VNPAY chưa thanh toán → yêu cầu thanh toán lại
    ORDER_VNPAY_FAILED,             // VNPAY thanh toán thất bại
    ORDER_VNPAY_EMPTY_RESULT,       // VNPAY khi quay lại màn hình mà chưa có transactionId
    ORDER_CANCELLED                 // Thanh toán quá 3 lần hủy dơn hàng
}