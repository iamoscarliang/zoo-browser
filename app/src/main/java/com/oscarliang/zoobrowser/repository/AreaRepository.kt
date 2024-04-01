package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AreaDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.Area
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreaRepository @Inject constructor(
    private val db: ZooDatabase,
    private val areaDao: AreaDao,
    private val zooService: ZooService
) {

    fun getAreas(): LiveData<Resource<List<Area>>> {
        return object : NetworkBoundResource<List<Area>>() {
            override fun query(): List<Area> {
                return areaDao.getAreas().value ?: emptyList()
            }

            override fun queryObservable(): LiveData<List<Area>> {
                return areaDao.getAreas()
            }

            override suspend fun fetch(): List<Area> {
                return zooService.getAreas().result.results
            }

            override suspend fun saveFetchResult(data: List<Area>) {
                areaDao.insertAreas(data)
            }
        }.asLiveData()
    }

}