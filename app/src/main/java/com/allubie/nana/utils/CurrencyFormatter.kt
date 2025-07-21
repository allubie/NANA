package com.allubie.nana.utils

object CurrencyFormatter {
    
    fun formatAmount(amount: Double, currencyFormat: String): String {
        val formattedAmount = String.format("%.2f", amount)
        
        return when (currencyFormat) {
            "USD ($)" -> "$$formattedAmount"
            "EUR (€)" -> "€$formattedAmount"
            "GBP (£)" -> "£$formattedAmount"
            "JPY (¥)" -> "¥${String.format("%.0f", amount)}" // No decimals for JPY
            "INR (₹)" -> "₹$formattedAmount"
            "BDT (৳)" -> "৳$formattedAmount"
            "CNY (¥)" -> "¥$formattedAmount"
            "KRW (₩)" -> "₩${String.format("%.0f", amount)}" // No decimals for KRW
            "CAD (C$)" -> "C$$formattedAmount"
            "AUD (A$)" -> "A$$formattedAmount"
            "CHF (Fr)" -> "Fr$formattedAmount"
            "SEK (kr)" -> "$formattedAmount kr"
            "NOK (kr)" -> "$formattedAmount kr"
            "DKK (kr)" -> "$formattedAmount kr"
            "RUB (₽)" -> "$formattedAmount ₽"
            "BRL (R$)" -> "R$$formattedAmount"
            "MXN ($)" -> "$$formattedAmount MXN"
            "ZAR (R)" -> "R$formattedAmount"
            "SGD (S$)" -> "S$$formattedAmount"
            "HKD (HK$)" -> "HK$$formattedAmount"
            "NZD (NZ$)" -> "NZ$$formattedAmount"
            else -> "$$formattedAmount" // Default to USD format
        }
    }
    
    fun getCurrencySymbol(currencyFormat: String): String {
        return when (currencyFormat) {
            "USD ($)" -> "$"
            "EUR (€)" -> "€"
            "GBP (£)" -> "£"
            "JPY (¥)" -> "¥"
            "INR (₹)" -> "₹"
            "BDT (৳)" -> "৳"
            "CNY (¥)" -> "¥"
            "KRW (₩)" -> "₩"
            "CAD (C$)" -> "C$"
            "AUD (A$)" -> "A$"
            "CHF (Fr)" -> "Fr"
            "SEK (kr)" -> "kr"
            "NOK (kr)" -> "kr"
            "DKK (kr)" -> "kr"
            "RUB (₽)" -> "₽"
            "BRL (R$)" -> "R$"
            "MXN ($)" -> "$"
            "ZAR (R)" -> "R"
            "SGD (S$)" -> "S$"
            "HKD (HK$)" -> "HK$"
            "NZD (NZ$)" -> "NZ$"
            else -> "$"
        }
    }
    
    fun getAllCurrencies(): List<String> {
        return listOf(
            "USD ($)",
            "EUR (€)",
            "GBP (£)",
            "JPY (¥)",
            "INR (₹)",
            "BDT (৳)",
            "CNY (¥)",
            "KRW (₩)",
            "CAD (C$)",
            "AUD (A$)",
            "CHF (Fr)",
            "SEK (kr)",
            "NOK (kr)",
            "DKK (kr)",
            "RUB (₽)",
            "BRL (R$)",
            "MXN ($)",
            "ZAR (R)",
            "SGD (S$)",
            "HKD (HK$)",
            "NZD (NZ$)"
        )
    }
}
