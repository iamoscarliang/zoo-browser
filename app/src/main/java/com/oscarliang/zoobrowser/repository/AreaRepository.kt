package com.oscarliang.zoobrowser.repository

import androidx.lifecycle.LiveData
import com.oscarliang.zoobrowser.api.AreaResponse
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AreaDao
import com.oscarliang.zoobrowser.model.Area
import com.oscarliang.zoobrowser.util.NetworkBoundResource
import com.oscarliang.zoobrowser.util.RateLimiter
import com.oscarliang.zoobrowser.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY = "area"

@Singleton
class AreaRepository @Inject constructor(
    private val areaDao: AreaDao,
    private val service: ZooService,
    private val rateLimiter: RateLimiter<String>
) {

    fun getAreas(): LiveData<Resource<List<Area>>> {
        return object : NetworkBoundResource<List<Area>, AreaResponse>() {
            override suspend fun query(): List<Area> {
                return areaDao.findAreas()
            }

            override fun queryObservable(): LiveData<List<Area>> {
                return areaDao.getAreas()
            }

            override suspend fun fetch(): AreaResponse {
                return service.getAreas()
            }

            override suspend fun saveFetchResult(data: AreaResponse) {
                areaDao.insertAreas(data.result.results)
            }

            override fun shouldFetch(data: List<Area>): Boolean {
                return rateLimiter.shouldFetch(KEY)
            }

            override fun onFetchFailed(exception: Exception) {
                rateLimiter.reset(KEY)
            }
        }.asLiveData()
    }

}