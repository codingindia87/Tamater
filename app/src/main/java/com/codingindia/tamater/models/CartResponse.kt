package com.codingindia.tamater.models

data class CartResponse(
    val cart: CartModel,
    val food: FoodModel,
    val id: String
)