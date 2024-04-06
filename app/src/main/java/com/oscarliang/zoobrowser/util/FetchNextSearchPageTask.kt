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
            val fetchedData = response.result.results

            // We merge all new search result into current result list
            val ids = arrayListOf<Int>()
            ids.addAll(current.animalIds)
            ids.addAll(fetchedData.map { it.id })
            val merged = AnimalSearchResult(
                query = query,
                count = response.result.count,
                animalIds = ids
            )
            db.animalDao().insertAnimals(fetchedData)
            db.animalDao().insertAnimalSearchResults(merged)
            emit(Resource.success(true))
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Unknown error", true))
        }
    }

}