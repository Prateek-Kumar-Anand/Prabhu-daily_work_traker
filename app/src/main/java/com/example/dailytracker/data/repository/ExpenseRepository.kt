package com.example.dailytracker.data.repository

import com.example.dailytracker.data.dao.ExpenseDao
import com.example.dailytracker.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<ExpenseEntity>> = dao.getBetween(start, end)
    fun totalBetween(start: Long, end: Long): Flow<Double> = dao.getTotalBetween(start, end)

    suspend fun add(expense: ExpenseEntity) = dao.insert(expense)
    suspend fun remove(expense: ExpenseEntity) = dao.delete(expense)
}
