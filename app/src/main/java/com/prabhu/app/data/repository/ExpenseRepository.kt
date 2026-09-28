package com.prabhu.app.data.repository

import com.prabhu.app.data.dao.ExpenseDao
import com.prabhu.app.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<ExpenseEntity>> = dao.getBetween(start, end)
    fun totalBetween(start: Long, end: Long): Flow<Double> = dao.getTotalBetween(start, end)

    suspend fun add(expense: ExpenseEntity) = dao.insert(expense)
    suspend fun remove(expense: ExpenseEntity) = dao.delete(expense)
}
