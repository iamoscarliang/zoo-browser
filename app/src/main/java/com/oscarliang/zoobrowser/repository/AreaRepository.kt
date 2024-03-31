package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AreaDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.Area
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.Resource

class AreaRepository(
    private val db: ZooDatabase,
    private val areaDao: AreaDao,
    private val zooService: ZooService
) {

    fun getAreas(): LiveData<Resource<List<Area>>> {
        return object : NetworkBoundResource<List<Area>>() {
            override fun query(): List<Area> {
                return areaDao.getAreas()
            }

            override fun queryObservable(): LiveData<List<Area>> {
                return areaDao.getObservableAreas()
            }

            override suspend fun fetch(): List<Area> {
                return zooService.getAreas().results
            }

            override suspend fun saveFetchResult(data: List<Area>) {
                areaDao.insertAreas(data)
            }
        }.asLiveData()
    }

}