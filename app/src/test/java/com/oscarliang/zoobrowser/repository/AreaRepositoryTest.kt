package com.oscarliang.zoobrowser.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.oscarliang.zoobrowser.api.AreaResponse
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AreaDao
import com.oscarliang.zoobrowser.model.Area
import com.oscarliang.zoobrowser.util.MainDispatcherRule
import com.oscarliang.zoobrowser.util.RateLimiter
import com.oscarliang.zoobrowser.util.Resource
import com.oscarliang.zoobrowser.util.TestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AreaRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dao = mock<AreaDao>()
    private val service = mock<ZooService>()
    private val rateLimiter = mock<RateLimiter<String>>()
    private lateinit var repository: AreaRepository

    @Before
    fun init() {
        repository = AreaRepository(
            areaDao = dao,
            service = service,
            rateLimiter = rateLimiter
        )
    }

    @Test
    fun testLoadFromDb() = runTest {
        whenever(rateLimiter.shouldFetch(any())).thenReturn(false)
        val dbData = MutableLiveData<List<Area>>()
        whenever(dao.getAreas()).thenReturn(dbData)
        val areas = TestUtil.createAreas(1, "foo", "bar")
        whenever(dao.findAreas()).thenReturn(areas)

        val observer = mock<Observer<Resource<List<Area>>>>()
        repository.getAreas().observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.loading(null))

        dbData.postValue(areas)
        verify(observer).onChanged(Resource.success(areas))
        verifyNoInteractions(service)
    }

    @Test
    fun testLoadFromNetwork() = runTest {
        whenever(rateLimiter.shouldFetch(any())).thenReturn(true)
        val dbData = MutableLiveData<List<Area>>()
        whenever(dao.getAreas()).thenReturn(dbData)
        val areas = TestUtil.createAreas(1, "foo", "bar")
        whenever(dao.findAreas()).thenReturn(areas)
        val response = AreaResponse(AreaResponse.Result(areas))
        whenever(service.getAreas()).thenReturn(response)

        val observer = mock<Observer<Resource<List<Area>>>>()
        repository.getAreas().observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.loading(null))

        dbData.postValue(areas)
        verify(service).getAreas()
        verify(dao).insertAreas(areas)
        verify(observer).onChanged(Resource.success(areas))
    }

    @Test
    fun testLoadFromNetworkError() = runTest {
        whenever(rateLimiter.shouldFetch(any())).thenReturn(true)
        val dbData = MutableLiveData<List<Area>>()
        whenever(dao.getAreas()).thenReturn(dbData)
        val areas = TestUtil.createAreas(1, "foo", "bar")
        whenever(dao.findAreas()).thenReturn(areas)
        whenever(service.getAreas()).thenAnswer { throw Exception("idk") }

        val observer = mock<Observer<Resource<List<Area>>>>()
        repository.getAreas().observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.loading(null))

        dbData.postValue(areas)
        verify(service).getAreas()
        verify(observer).onChanged(Resource.error("idk", areas))
    }

}