package com.oscarliang.zoobrowser.util

import androidx.lifecycle.liveData
import androidx.room.withTransaction
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.AnimalSearchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchNextSearchPageTask(
    private val query: String,
    private val limit: Int,
    private val zooService: ZooService,
    private val db: ZooDatabase,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun asLiveData() = liveData {
        val result = withContext(ioDispatcher) {
            db.animalDao().findAnimalSearchResult(query)
        }
        if (result == null) {
            emit(null)
            return@liveData
        }
        val current = result.animalIds.size
        if (current == 0 || current % limit != 0) {
            emit(Resource.success(false))
            return@liveData
        }

        try {
            val fetchData = withContext(ioDispatcher) {
                zooService.searchAnimals(
                    query = query,
                    limit = limit,
                    offset = current
                ).result.results
            }

            if (fetchData.isEmpty()) {
                emit(Resource.success(false))
                return@liveData
            }

            // We merge all new search result into current result list
            val ids = arrayListOf<Int>()
            ids.addAll(result.animalIds)
            ids.addAll(fetchData.map { it.id })
            val merged = AnimalSearchResult(query, ids)
            withContext(ioDispatcher) {
                db.withTransaction {
                    db.animalDao().insertAnimals(fetchData)
                    db.animalDao().insertAnimalSearchResults(merged)
                }
            }
            emit(Resource.success(fetchData.size == limit))
        } catch (e: IOException) {
            emit(Resource.error(e.message!!, true))
        }
    }

}