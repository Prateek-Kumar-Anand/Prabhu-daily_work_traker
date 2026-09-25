package com.example.dailytracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dailytracker.data.model.ClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Insert
    suspend fun insert(classEntry: ClassEntity): Long

    @Update
    suspend fun update(classEntry: ClassEntity)

    @Delete
    suspend fun delete(classEntry: ClassEntity)

    @Query("SELECT * FROM classes ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun getBetween(start: Long, end: Long): Flow<List<ClassEntity>>

    @Query("SELECT COUNT(*) FROM classes WHERE dateMillis BETWEEN :start AND :end")
    fun getCountBetween(start: Long, end: Long): Flow<Int>
}
