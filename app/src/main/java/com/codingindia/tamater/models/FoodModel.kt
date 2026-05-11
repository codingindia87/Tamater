package com.codingindia.tamater.models

data class FoodModel(
    val name:String = "",
    val description: String = "",
    val image: String = "",
    val type: String = "veg",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    var id:String = "",
    var isSave: Boolean = false
)