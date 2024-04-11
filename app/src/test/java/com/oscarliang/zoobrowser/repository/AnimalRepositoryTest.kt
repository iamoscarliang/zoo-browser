package com.oscarliang.zoobrowser.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.oscarliang.zoobrowser.api.AnimalResponse
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AnimalDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.AnimalSearchResult
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
class AnimalRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dao = mock<AnimalDao>()
    private val service = mock<ZooService>()
    private val rateLimiter = mock<RateLimiter<String>>()
    private lateinit var repository: AnimalRepository

    @Before
    fun init() {
        val db = mock<ZooDatabase>()
        whenever(db.animalDao()).thenReturn(dao)
        repository = AnimalRepository(
            db = db,
            animalDao = dao,
            service = service,
            rateLimiter = rateLimiter
        )
    }

    @Test
    fun testSearchFromDb() = runTest {
        whenever(rateLimiter.shouldFetch(any())).thenReturn(false)
        val ids = listOf(0, 1)
        val dbSearchResult = MutableLiveData<AnimalSearchResult>()
        whenever(dao.getAnimalSearchResult("foo")).thenReturn(dbSearchResult)
        val dbData = MutableLiveData<List<Animal>>()
        whenever(dao.getOrdered(ids)).thenReturn(dbData)
        val animals = TestUtil.createAnimals(2, "foo", "bar")
        whenever(dao.findAnimals("foo")).thenReturn(animals)

        val observer = mock<Observer<Resource<List<Animal>>>>()
        repository.search("foo", 10).observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.loading(null))

        val searchResult = AnimalSearchResult("foo", 2, ids)
        dbSearchResult.postValue(searchResult)
        dbData.postValue(animals)
        verify(observer).onChanged(Resource.success(animals))
        verifyNoInteractions(service)
    }

    @Test
    fun testSearchFromNetwork() = runTest {
        whenever(rateLimiter.shouldFetch(any())).thenReturn(true)
        val ids = listOf(0, 1)
        val dbSearchResult = MutableLiveData<AnimalSearchResult>()
        whenever(dao.getAnimalSearchResult("foo")).thenReturn(dbSearchResult)
        val dbData = MutableLiveData<List<Animal>>()
        whenever(dao.getOrdered(ids)).thenReturn(dbData)
        val animals = TestUtil.createAnimals(2, "foo", "bar")
        whenever(dao.findAnimals("foo")).thenReturn(animals)
        whenever(dao.findBookmarks()).thenReturn(listOf())
        val response = AnimalResponse(AnimalResponse.Result(2, animals))
        whenever(service.searchAnimals("foo", 10)).thenReturn(response)

        val observer = mock<Observer<Resource<List<Animal>>>>()
        repository.search("foo", 10).observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.loading(null))

        val searchResult = AnimalSearchResult("foo", 2, ids)
        dbSearchResult.postValue(searchResult)
        dbData.postValue(animals)
        verify(service).searchAnimals("foo", 10)
        verify(dao).insertAnimals(animals)
        verify(dao).insertAnimalSearchResults(searchResult)
        verify(observer).onChanged(Resource.success(animals))
    }

    @Test
    fun testSearchFromNetworkError() = runTest {
        whenever(rateLimiter.shouldFetch(any())).thenReturn(true)
        val ids = listOf(0, 1)
        val dbSearchResult = MutableLiveData<AnimalSearchResult>()
        whenever(dao.getAnimalSearchResult("foo")).thenReturn(dbSearchResult)
        val dbData = MutableLiveData<List<Animal>>()
        whenever(dao.getOrdered(ids)).thenReturn(dbData)
        val animals = TestUtil.createAnimals(2, "foo", "bar")
        whenever(dao.findAnimals("foo")).thenReturn(animals)
        whenever(dao.findBookmarks()).thenReturn(listOf())
        whenever(service.searchAnimals("foo", 10)).thenAnswer { throw Exception("idk") }

        val observer = mock<Observer<Resource<List<Animal>>>>()
        repository.search("foo", 10).observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.loading(null))

        val searchResult = AnimalSearchResult("foo", 2, ids)
        dbSearchResult.postValue(searchResult)
        dbData.postValue(animals)
        verify(service).searchAnimals("foo", 10)
        verify(observer).onChanged(Resource.error("idk", animals))
    }

    @Test
    fun testSearchNextPageNull() = runTest {
        whenever(dao.findAnimalSearchResult("foo")).thenReturn(null)
        val observer = mock<Observer<Resource<Boolean>?>>()
        repository.searchNextPage("foo", 10).observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(null)
    }

    @Test
    fun testSearchNextPageFalse() = runTest {
        val ids = listOf(1, 2)
        val searchResult = AnimalSearchResult("foo", 2, ids)
        whenever(dao.findAnimalSearchResult("foo")).thenReturn(searchResult)
        val observer = mock<Observer<Resource<Boolean>?>>()
        repository.searchNextPage("foo", 10).observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.success(false))
    }

    @Test
    fun testSearchNextPageTrue() = runTest {
        val ids = listOf(1, 2)
        val searchResult = AnimalSearchResult("foo", 10, ids)
        whenever(dao.findAnimalSearchResult("foo")).thenReturn(searchResult)
        val animals = TestUtil.createAnimals(2, "foo", "bar")
        whenever(dao.findBookmarks()).thenReturn(listOf())
        val response = AnimalResponse(AnimalResponse.Result(2, animals))
        whenever(service.searchAnimals("foo", 10, 2)).thenReturn(response)

        val observer = mock<Observer<Resource<Boolean>?>>()
        repository.searchNextPage("foo", 10).observeForever(observer)
        advanceUntilIdle()
        verify(observer).onChanged(Resource.success(true))
    }

}