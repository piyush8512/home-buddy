package com.example.buddy.data

import kotlinx.coroutines.flow.Flow

class PantryRepository(private val pantryDao: PantryDao) {
    val allActiveItems: Flow<List<PantryItem>> = pantryDao.getActiveItems()

    fun getItemsByZone(zone: String): Flow<List<PantryItem>> = pantryDao.getItemsByZone(zone)

    suspend fun insertItem(item: PantryItem): Long = pantryDao.insertItem(item)

    suspend fun updateItem(item: PantryItem) = pantryDao.updateItem(item)

    suspend fun deleteItem(item: PantryItem) = pantryDao.deleteItem(item)

    suspend fun deleteById(id: Long) = pantryDao.deleteById(id)
}
