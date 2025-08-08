package com.allubie.nana.utils

fun getCurrencySymbol(currency: String): String {
    return when (currency) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "CAD" -> "C$"
        "AUD" -> "A$"
        "CHF" -> "CHF"
        "CNY" -> "¥"
        "INR" -> "₹"
        "BDT" -> "৳"
        else -> "$" // Default
    }
}
