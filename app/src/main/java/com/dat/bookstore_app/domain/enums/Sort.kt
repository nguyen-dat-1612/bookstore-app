package com.dat.bookstore_app.domain.enums

enum class Sort(val queryParam: String) {
    SOLD_DESC("sold,desc"),
    SOLD_ASC("sold,asc"),
    NEW_DESC("updatedAt,desc"),
    NEW_ASC("updatedAt,asc"),
    RATING_DESC("rating,desc"),
    RATING_ASC("rating,asc"),
    PRICE_ASC("price,asc"),
    PRICE_DESC("price,desc");

    override fun toString(): String = queryParam
}