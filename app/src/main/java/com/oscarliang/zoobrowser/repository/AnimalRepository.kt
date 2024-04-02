package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AnimalDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.util.FetchNextSearchPageTask
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(
    private val db: ZooDatabase,
    private val animalDao: AnimalDao,
    private val zooService: ZooService
) {

    fun search(query: String, limit: Int): LiveData<Resource<List<Animal>>> {
        return object : NetworkBoundResource<List<Animal>>() {
            override fun query(): List<Animal> {
                return animalDao.getAnimals(query).value ?: emptyList()
            }

            override fun queryObservable(): LiveData<List<Animal>> {
                return animalDao.getAnimals(query)
            }

            override suspend fun fetch(): List<Animal> {
                return zooService.searchAnimals(query, limit).result.results
            }

            override suspend fun saveFetchResult(data: List<Animal>) {
                animalDao.insertAnimals(data)
            }
        }.asLiveData()
    }

    fun searchNextPage(query: String, limit: Int): LiveData<Resource<Boolean>?> {
        return FetchNextSearchPageTask(
            query = query,
            limit = limit,
            zooService = zooService,
            db = db
        ).asLiveData()
    }

    fun getBookmarks(): LiveData<List<Animal>> {
        return animalDao.getBookmarks()
    }

    suspend fun updateAnimal(animal: Animal) {
        animalDao.updateAnimal(animal)
    }

}