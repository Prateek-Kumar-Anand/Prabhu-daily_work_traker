package com.example.dailytracker.data.model

/** Distinguishes the three kinds of records the app tracks. */
enum class EntryType {
    SPENDING,
    CLASS,
    ACTIVITY
}

/** Fixed category options offered in the "Add Spending" form. */
val expenseCategories = listOf("Food", "Transport", "Books & Supplies", "Fees", "Entertainment", "Other")

/** Fixed category options offered in the "Add Class" form. */
val classCategories = listOf("Lecture", "Lab", "Tutorial", "Seminar", "Exam", "Other")

/** Fixed category options offered in the "Add Activity" form. */
val activityCategories = listOf("Study", "Assignment", "Exam Prep", "Personal", "Errand", "Other")
