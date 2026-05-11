package com.codingindia.tamater.utils

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale



object Converters {

    fun priceFormat(price: Double):String{
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("INR")
        return format.format(price)
    }


    fun timestampToDateTime(timestamp: Timestamp?): String {
        if (timestamp != null) {
            val date: Date = timestamp.toDate()
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm a", Locale.getDefault())
            return sdf.format(date)
        } else {
            return ""
        }
    }

}