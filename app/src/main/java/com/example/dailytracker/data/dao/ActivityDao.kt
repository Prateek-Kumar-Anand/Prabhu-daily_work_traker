package com.example.dailytracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dailytracker.data.model.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: ActivityEntity)

    @Update
    suspend fun update(activity: ActivityEntity)

    @Delete
    suspend fun delete(activity: ActivityEntity)

    @Query("SELECT * FROM activities ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun getBetween(start: Long, end: Long): Flow<List<ActivityEntity>>

    @Query("SELECT COUNT(*) FROM activities WHERE dateMillis BETWEEN :start AND :end")
    fun getCountBetween(start: Long, end: Long): Flow<Int>
}
