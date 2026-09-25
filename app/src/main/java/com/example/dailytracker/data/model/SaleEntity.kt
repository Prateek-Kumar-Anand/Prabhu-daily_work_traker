package com.example.dailytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
    val amount: Double,
    val quantity: Int,
    val customer: String,
    val note: String,
    val dateMillis: Long
)
