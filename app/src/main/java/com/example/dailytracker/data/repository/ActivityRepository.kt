package com.example.dailytracker.data.repository

import com.example.dailytracker.data.dao.ActivityDao
import com.example.dailytracker.data.model.ActivityEntity
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val dao: ActivityDao) {
    val allActivities: Flow<List<ActivityEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<ActivityEntity>> = dao.getBetween(start, end)
    fun countBetween(start: Long, end: Long): Flow<Int> = dao.getCountBetween(start, end)

    suspend fun add(activity: ActivityEntity) = dao.insert(activity)
    suspend fun update(activity: ActivityEntity) = dao.update(activity)
    suspend fun remove(activity: ActivityEntity) = dao.delete(activity)
}
