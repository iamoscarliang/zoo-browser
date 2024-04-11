package com.oscarliang.zoobrowser.util

import androidx.lifecycle.liveData
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.AnimalSearchResult

class FetchNextSearchPageTask(
    private val query: String,
    private val limit: Int,
    private val db: ZooDatabase,
    private val zooService: ZooService
) {

    fun asLiveData() = liveData {
        val current = db.animalDao().findAnimalSearchResult(query)
        if (current == null) {
            emit(null)
            return@liveData
        }
        val currentCount = current.animalIds.size
        if (currentCount >= current.count) {
            emit(Resource.success(false))
            return@liveData
        }

        try {
            val response = zooService.searchAnimals(
                query = query,
                limit = limit,
                offset = currentCount
            )
            val animals = response.result.results
            val bookmarks = db.animalDao().findBookmarks()
            animals.forEach { newData ->
                // We prevent overriding bookmark field
                newData.bookmark = bookmarks.any { currentData ->
                    currentData.id == newData.id
                }
            }

            // We merge all new search result into current result list
            val animalIds = mutableListOf<Int>()
            animalIds.addAll(current.animalIds)
            animalIds.addAll(animals.map { it.id })
            val merged = AnimalSearchResult(
                query = query,
                count = response.result.count,
                animalIds = animalIds
            )
            db.animalDao().insertAnimals(animals)
            db.animalDao().insertAnimalSearchResults(merged)
            emit(Resource.success(true))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error", true))
        }
    }

}