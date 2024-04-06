package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.oscarliang.zoobrowser.api.AnimalResponse
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AnimalDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.AnimalSearchResult
import com.oscarliang.zoobrowser.util.AbsentLiveData
import com.oscarliang.zoobrowser.util.FetchNextSearchPageTask
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.RateLimiter
import com.oscarliang.zoobrowser.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY = "animal"

@Singleton
class AnimalRepository @Inject constructor(
    private val db: ZooDatabase,
    private val animalDao: AnimalDao,
    private val service: ZooService,
    private val rateLimiter: RateLimiter<String>
) {

    fun search(query: String, limit: Int): LiveData<Resource<List<Animal>>> {
        return object : NetworkBoundResource<List<Animal>, AnimalResponse>() {
            override suspend fun query(): List<Animal> {
                return animalDao.findAnimals(query)
            }

            override fun queryObservable(): LiveData<List<Animal>> {
                return animalDao.getAnimalSearchResult(query).switchMap { searchResult ->
                    if (searchResult == null) {
                        AbsentLiveData.create()
                    } else {
                        animalDao.getOrdered(searchResult.animalIds)
                    }
                }
            }

            override suspend fun fetch(): AnimalResponse {
                return service.searchAnimals(query, limit)
            }

            override suspend fun saveFetchResult(data: AnimalResponse) {
                val animals = data.result.results
                val bookmarks = animalDao.findBookmarks()
                animals.forEach { newData ->
                    // We prevent overriding bookmark field
                    newData.bookmark = bookmarks.any { currentData ->
                        currentData.id == newData.id
                    }
                }
                val animalIds = animals.map { it.id }
                val animalSearchResult = AnimalSearchResult(
                    query = query,
                    count = data.result.count,
                    animalIds = animalIds
                )
                animalDao.insertAnimals(animals)
                animalDao.insertAnimalSearchResults(animalSearchResult)
            }

            override fun shouldFetch(data: List<Animal>): Boolean {
                return rateLimiter.shouldFetch(KEY)
            }

            override fun onFetchFailed(exception: Exception) {
                rateLimiter.reset(KEY)
            }
        }.asLiveData()
    }

    fun searchNextPage(query: String, limit: Int): LiveData<Resource<Boolean>?> {
        return FetchNextSearchPageTask(
            query = query,
            limit = limit,
            db = db,
            zooService = service
        ).asLiveData()
    }

    fun getBookmarks(): LiveData<List<Animal>> {
        return animalDao.getBookmarks()
    }

    fun getAnimalById(id: Int): LiveData<Animal> {
        return animalDao.getAnimalById(id)
    }

    suspend fun updateAnimal(animal: Animal) {
        animalDao.updateAnimal(animal)
    }

}