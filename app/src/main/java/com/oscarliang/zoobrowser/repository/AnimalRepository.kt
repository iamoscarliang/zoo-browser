package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.room.withTransaction
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AnimalDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.AnimalSearchResult
import com.oscarliang.zoobrowser.util.AbsentLiveData
import com.oscarliang.zoobrowser.util.FetchNextSearchPageTask
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(
    private val db: ZooDatabase,
    private val animalDao: AnimalDao,
    private val zooService: ZooService,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun search(query: String, limit: Int): LiveData<Resource<List<Animal>>> {
        return object : NetworkBoundResource<List<Animal>>() {
            override suspend fun query(): List<Animal> {
                return withContext(ioDispatcher) {
                    animalDao.findAnimals(query)
                }
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

            override suspend fun fetch(): List<Animal> {
                return withContext(ioDispatcher) {
                    zooService.searchAnimals(query, limit).result.results
                }
            }

            override suspend fun saveFetchResult(data: List<Animal>) {
                val animalIds = data.map { it.id }
                val animalSearchResult = AnimalSearchResult(query, animalIds)
                withContext(ioDispatcher) {
                    db.withTransaction {
                        animalDao.insertAnimals(data)
                        animalDao.insertAnimalSearchResults(animalSearchResult)
                    }
                }
            }
        }.asLiveData()
    }

    fun searchNextPage(query: String, limit: Int): LiveData<Resource<Boolean>?> {
        return FetchNextSearchPageTask(
            query = query,
            limit = limit,
            zooService = zooService,
            db = db,
            ioDispatcher = ioDispatcher
        ).asLiveData()
    }

    fun getBookmarks(): LiveData<List<Animal>> {
        return animalDao.getBookmarks()
    }

    suspend fun updateAnimal(animal: Animal) {
        animalDao.updateAnimal(animal)
    }

}