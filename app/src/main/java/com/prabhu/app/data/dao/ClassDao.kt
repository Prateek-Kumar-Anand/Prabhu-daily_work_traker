package com.prabhu.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.prabhu.app.data.model.ClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Insert
    suspend fun insert(classEntry: ClassEntity): Long

    @Update
    suspend fun update(classEntry: ClassEntity)

    @Delete
    suspend fun delete(classEntry: ClassEntity)

    @Query("DELETE FROM classes WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT * FROM classes WHERE subject = :subject COLLATE NOCASE ORDER BY dateMillis ASC")
    suspend fun getBySubject(subject: String): List<ClassEntity>

    @Query("SELECT * FROM classes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ClassEntity?

    @Query("SELECT * FROM classes ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun getBetween(start: Long, end: Long): Flow<List<ClassEntity>>

    @Query("SELECT COUNT(*) FROM classes WHERE dateMillis BETWEEN :start AND :end")
    fun getCountBetween(start: Long, end: Long): Flow<Int>
}
