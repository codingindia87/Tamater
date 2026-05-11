package com.codingindia.tamater.models

import com.google.firebase.Timestamp

data class OrderModel(
    var id: String? = null,
    val foods: MutableList<String>? = null,
    val orderBy: String? = "",
    var paymentMethod: String? = "",
    val timestamp: Timestamp? = Timestamp.now(),
    val orderPrice: Double? = 0.0,
    val name: String? = null,
    val address: String? = null,
    val city: String? = null,
    val pinCode: String? = null,
    val status: String? = "making",
    var orderId: String? = "",
    var paymentStatus: String? = "pending"
)
