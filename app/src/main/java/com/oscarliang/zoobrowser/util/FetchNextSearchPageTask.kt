package com.oscarliang.zoobrowser.util

import androidx.lifecycle.liveData
import androidx.room.withTransaction
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.AnimalSearchResult
import java.io.IOException

class FetchNextSearchPageTask(
    private val query: String,
    private val limit: Int,
    private val zooService: ZooService,
    private val db: ZooDatabase
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
            db.withTransaction {
                db.animalDao().insertAnimals(fetchedData)
                db.animalDao().insertAnimalSearchResults(merged)
            }
            emit(Resource.success(true))
        } catch (e: IOException) {
            emit(Resource.error(e.message!!, true))
        }
    }

}