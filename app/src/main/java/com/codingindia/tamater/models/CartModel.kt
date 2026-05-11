package com.codingindia.tamater.models

import com.google.firebase.Timestamp

data class CartModel(
    val id: String = "",
    var quantity: Int = 1,
    val timestamp: Timestamp = Timestamp.now()
)