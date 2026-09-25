package com.example.dailytracker.util

import java.text.NumberFormat
import java.util.Locale

fun formatMoney(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return format.format(amount)
}
