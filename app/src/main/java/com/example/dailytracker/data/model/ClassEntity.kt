package com.example.dailytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val type: String,
    val teacher: String,
    val room: String,
    val note: String,
    val dateMillis: Long,
    val attended: Boolean = true
)
