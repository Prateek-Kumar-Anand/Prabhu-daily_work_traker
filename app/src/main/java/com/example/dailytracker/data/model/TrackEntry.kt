package com.example.dailytracker.data.model

/**
 * A single, screen-agnostic representation of a spending, sale, or activity
 * record, used to render mixed lists (dashboard "recent" cards, History screen).
 */
data class TrackEntry(
    val id: Long,
    val type: EntryType,
    val title: String,
    val subtitle: String,
    val amount: Double?,   // null for activities
    val dateMillis: Long
)

fun ExpenseEntity.toTrackEntry() = TrackEntry(
    id = id,
    type = EntryType.SPENDING,
    title = category,
    subtitle = note.ifBlank { "Spending" },
    amount = amount,
    dateMillis = dateMillis
)

fun SaleEntity.toTrackEntry() = TrackEntry(
    id = id,
    type = EntryType.SALE,
    title = itemName,
    subtitle = if (customer.isBlank()) "Qty $quantity" else "$customer · Qty $quantity",
    amount = amount,
    dateMillis = dateMillis
)

fun ActivityEntity.toTrackEntry() = TrackEntry(
    id = id,
    type = EntryType.ACTIVITY,
    title = title,
    subtitle = category,
    amount = null,
    dateMillis = dateMillis
)
