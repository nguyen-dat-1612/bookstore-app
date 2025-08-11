package com.dat.bookstore_app.data.mapper

import androidx.core.text.HtmlCompat
import com.dat.bookstore_app.data.datasource.remote.dto.AddressResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.BookResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.CartResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.CategoryResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.FileResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.OrderItemRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.OrderItemResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.OrderResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedBookResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedFavoriteResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedOrderResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PaymentResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PaymentResultDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PermissionResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.UserLogin
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.models.Category
import com.dat.bookstore_app.domain.models.CategoryUiModel
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.models.File
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.OrderItem
import com.dat.bookstore_app.domain.models.PagedBook
import com.dat.bookstore_app.domain.models.PagedFavorite
import com.dat.bookstore_app.domain.models.PagedOrder
import com.dat.bookstore_app.domain.models.Payment
import com.dat.bookstore_app.domain.models.PaymentResult
import com.dat.bookstore_app.domain.models.Permission
import com.dat.bookstore_app.domain.models.User

// DTO -> Domain

fun BookResponseDTO.toDomain(): Book {
    return Book(
        id = id ?: 0L,
        thumbnail = thumbnail.orEmpty(),
        slider = slider ?: emptyList(),
        title = title.orEmpty(),
        author = author.orEmpty(),
        price = price ?: 0.0,
        quantity = quantity ?: 0,
        category = category?.toDomain() ?: error("Category is null"),
        description = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
        createdAt = createdAt.orEmpty(),
        updatedAt = updatedAt.orEmpty(),
        discount = discount ?: 0,
        sold = sold ?: 0,
        age = age ?: 0,
        publicationDate = publicationDate.orEmpty(),
        publisher = publisher.orEmpty(),
        language = language.orEmpty(),
        pageCount = pageCount ?: 0,
        coverType = coverType.orEmpty()
    )
}

fun CategoryResponseDTO.toDomain(): Category {
    return Category(
        id = id ?: 0L,
        name = name.orEmpty(),
        description = description.orEmpty(),
        createdAt = createdAt.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )
}

fun List<CategoryResponseDTO>.toCategoryDomain(): List<Category> {
    return map { it.toDomain() }
}

fun List<BookResponseDTO>.toBookDomain(): List<Book> {
    return map { it.toDomain() }
}

fun PagedBookResponseDTO.toDomain(): PagedBook {
    return PagedBook(
        books = books.map { it.toDomain() },
        current = paginationDTO.current,
        pageSize = paginationDTO.pageSize,
        pages = paginationDTO.pages,
        total = paginationDTO.total
    )
}

fun UserLogin.toDomain(): User {
    return User(
        email = email,
        phone = phone,
        fullName = fullName,
        address = address,
        role = role,
        id = id,
        avatar = avatar,
        permissions = permissions.map { it.toDomain() },
        noPassword = noPassword
    )
}

fun PermissionResponseDTO.toDomain(): Permission {
    return Permission(
        name = name,
        path = path,
        method = method,
        module = module
    )
}

fun CartResponseDTO.toCartDomain(): Cart {
    return Cart(
        id = id,
        quantity = quantity,
        createdAt = createdAt,
        updatedAt = updatedAt,
        book = book.toDomain()
    )
}

fun List<CartResponseDTO>.toDomain(): List<Cart> {
    return map { it.toCartDomain() }
}

fun Category.toUiModel(isSelected: Boolean = false): CategoryUiModel {
    return CategoryUiModel(
        id = this.id,
        name = this.name,
        isSelected = isSelected
    )
}

fun PagedOrderResponseDTO.toDomain(): PagedOrder {
    return PagedOrder(
        orders = orders.map { it.toDomain() },
        current = paginationDTO.current,
        pageSize = paginationDTO.pageSize,
        pages = paginationDTO.pages,
        total = paginationDTO.total
    )
}

fun OrderResponseDTO.toDomain() : Order {
    return Order(
        id = id,
        fullName = fullName,
        phone = phone,
        totalAmount = totalAmount,
        status = status,
        shippingAddress = shippingAddress,
        paymentMethod = paymentMethod,
        userId = userId,
        orderItems = orderItems.map { it.toDomain() },
        createdAt = createdAt.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )
}

fun OrderItemResponseDTO.toDomain(): OrderItem {
    return OrderItem(
        id = id,
        quantity = quantity,
        price = price,
        book = book.toDomain()
    )
}

fun PaymentResponseDTO.toDomain() : Payment {
    return Payment(
        paymentUrl = paymentUrl,
        transactionId = transactionId,
        paymentMethod = paymentMethod,
        status = status,
        message = message
    )
}

fun PaymentResultDTO.toDomain() : PaymentResult {
    return PaymentResult(
        transactionId = transactionId,
        status = status,
        message = message
    )
}

fun List<Cart>.toOrderItemRequestDTO(): List<OrderItemRequestDTO> {
    return map {
        OrderItemRequestDTO(
            bookId = it.book.id,
            quantity = it.quantity
        )
    }
}

fun PagedFavoriteResponseDTO.toDomain() : PagedFavorite {
    return PagedFavorite(
        favorites = favorites.map { it.toDomain()},
        current = paginationDTO.current,
        pageSize = paginationDTO.pageSize,
        pages = paginationDTO.pages,
        total = paginationDTO.total
    )
}

fun FavoriteResponseDTO.toDomain() : Favorite {
    return Favorite(
        id = id,
        userId = userId,
        book = book.toDomain()
    )
}

fun FileResponseDTO.toDomain() : File {
    return File(
        url = url,
        fileName = fileName,
        uploadedAt = uploadedAt
    )
}

fun AddressResponseDTO.toDomain() : Address {
    return Address(
        id = id,
        fullName = fullName,
        phoneNumber = phoneNumber,
        province = province,
        ward = ward,
        addressDetail = addressDetail,
        addressType = addressType,
        isDefault = isDefault,
        createdAt = createdAt,
        updatedAt = updatedAt.orEmpty()
    )
}

fun List<AddressResponseDTO>.toAddressDomain() : List<Address> {
    return map { it.toDomain() }
}



