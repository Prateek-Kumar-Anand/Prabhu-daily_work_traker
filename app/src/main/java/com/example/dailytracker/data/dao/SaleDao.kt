package com.example.dailytracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.dailytracker.data.model.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Insert
    suspend fun insert(sale: SaleEntity)

    @Delete
    suspend fun delete(sale: SaleEntity)

    @Query("SELECT * FROM sales ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun getBetween(start: Long, end: Long): Flow<List<SaleEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM sales WHERE dateMillis BETWEEN :start AND :end")
    fun getTotalBetween(start: Long, end: Long): Flow<Double>
}
