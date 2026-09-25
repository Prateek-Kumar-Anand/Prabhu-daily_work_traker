package com.example.dailytracker.data.repository

import com.example.dailytracker.data.dao.SaleDao
import com.example.dailytracker.data.model.SaleEntity
import kotlinx.coroutines.flow.Flow

class SaleRepository(private val dao: SaleDao) {
    val allSales: Flow<List<SaleEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<SaleEntity>> = dao.getBetween(start, end)
    fun totalBetween(start: Long, end: Long): Flow<Double> = dao.getTotalBetween(start, end)

    suspend fun add(sale: SaleEntity) = dao.insert(sale)
    suspend fun remove(sale: SaleEntity) = dao.delete(sale)
}
