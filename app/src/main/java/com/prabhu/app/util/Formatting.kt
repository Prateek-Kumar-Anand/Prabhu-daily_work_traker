package com.prabhu.app.util

import java.text.NumberFormat
import java.util.Locale

// Building a currency NumberFormat is slow; keep one per thread instead of
// creating a new one for every row that is drawn while scrolling.
private val moneyFormat = object : ThreadLocal<NumberFormat>() {
    override fun initialValue(): NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
}

fun formatMoney(amount: Double): String = moneyFormat.get()!!.format(amount)
