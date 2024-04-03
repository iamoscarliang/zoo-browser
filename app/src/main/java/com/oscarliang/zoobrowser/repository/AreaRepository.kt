package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AreaDao
import com.oscarliang.zoobrowser.model.Area
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreaRepository @Inject constructor(
    private val areaDao: AreaDao,
    private val zooService: ZooService,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun getAreas(): LiveData<Resource<List<Area>>> {
        return object : NetworkBoundResource<List<Area>>() {
            override suspend fun query(): List<Area> {
                return withContext(ioDispatcher) {
                    areaDao.findAreas()
                }
            }

            override fun queryObservable(): LiveData<List<Area>> {
                return areaDao.getAreas()
            }

            override suspend fun fetch(): List<Area> {
                return withContext(ioDispatcher) {
                    zooService.getAreas().result.results
                }
            }

            override suspend fun saveFetchResult(data: List<Area>) {
                withContext(ioDispatcher) {
                    areaDao.insertAreas(data)
                }
            }
        }.asLiveData()
    }

}