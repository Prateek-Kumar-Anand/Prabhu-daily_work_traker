package com.example.dailytracker.data.model

/** Distinguishes the three kinds of records the app tracks. */
enum class EntryType {
    SPENDING,
    SALE,
    ACTIVITY
}

/** Fixed category options offered in the "Add Spending" form. */
val expenseCategories = listOf("Food", "Transport", "Shopping", "Bills", "Health", "Other")

/** Fixed category options offered in the "Add Activity" form. */
val activityCategories = listOf("Work", "Personal", "Health", "Errand", "Other")
