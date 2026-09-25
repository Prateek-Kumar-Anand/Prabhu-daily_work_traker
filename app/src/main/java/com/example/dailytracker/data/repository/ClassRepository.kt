package com.example.dailytracker.data.repository

import com.example.dailytracker.data.dao.ClassDao
import com.example.dailytracker.data.model.ClassEntity
import kotlinx.coroutines.flow.Flow

class ClassRepository(private val dao: ClassDao) {
    val allClasses: Flow<List<ClassEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<ClassEntity>> = dao.getBetween(start, end)
    fun countBetween(start: Long, end: Long): Flow<Int> = dao.getCountBetween(start, end)

    suspend fun add(classEntry: ClassEntity): Long = dao.insert(classEntry)
    suspend fun update(classEntry: ClassEntity) = dao.update(classEntry)
    suspend fun remove(classEntry: ClassEntity) = dao.delete(classEntry)
}
