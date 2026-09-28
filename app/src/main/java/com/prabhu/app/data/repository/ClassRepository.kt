package com.prabhu.app.data.repository

import com.prabhu.app.data.dao.ClassDao
import com.prabhu.app.data.model.ClassEntity
import kotlinx.coroutines.flow.Flow

class ClassRepository(private val dao: ClassDao) {
    val allClasses: Flow<List<ClassEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<ClassEntity>> = dao.getBetween(start, end)
    fun countBetween(start: Long, end: Long): Flow<Int> = dao.getCountBetween(start, end)

    suspend fun add(classEntry: ClassEntity): Long = dao.insert(classEntry)
    suspend fun update(classEntry: ClassEntity) = dao.update(classEntry)
    suspend fun remove(classEntry: ClassEntity) = dao.delete(classEntry)
    suspend fun getById(id: Long): ClassEntity? = dao.getById(id)
    suspend fun getBySubject(subject: String): List<ClassEntity> = dao.getBySubject(subject)
    suspend fun removeByIds(ids: List<Long>) {
        ids.chunked(500).forEach { dao.deleteByIds(it) }
    }
}
