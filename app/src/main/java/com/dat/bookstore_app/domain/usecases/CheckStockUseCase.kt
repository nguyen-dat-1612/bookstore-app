package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.OrderItemRequestDTO
import com.dat.bookstore_app.domain.repository.BookRepository
import javax.inject.Inject

//class CheckStockUseCase @Inject constructor(
//    private val bookRepository: BookRepository
//) {
//    suspend operator fun invoke(orderItems: List<OrderItemRequestDTO>): Result<Any?> {
//        val bookIds = orderItems.map { it.bookId }
//        val books = bookRepository.get(bookIds)
//
//        return orderItems.map { item ->
//            val matchedBook = books.find { it.id == item.bookId }
//            if (matchedBook != null) {
//                CheckStockResponseDTO(
//                    bookId = item.bookId,
//                    available = item.quantity <= matchedBook.stock,
//                    availableQuantity = matchedBook.stock
//                )
//            } else {
//                CheckStockResponseDTO(
//                    bookId = item.bookId,
//                    available = false,
//                    availableQuantity = 0
//                )
//            }
//        }
//    }
//}