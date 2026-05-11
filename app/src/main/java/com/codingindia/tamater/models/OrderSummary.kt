package com.codingindia.tamater.models

data class OrderSummary(
    val foods: ArrayList<CartResponse>? = null,
    val totalPrice: Double? = null
)
