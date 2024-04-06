package com.oscarliang.zoobrowser.ui.area

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.oscarliang.zoobrowser.repository.AnimalRepository
import com.oscarliang.zoobrowser.util.MainDispatcherRule
import com.oscarliang.zoobrowser.util.Resource
import com.oscarliang.zoobrowser.util.TestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class AreaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock<AnimalRepository>()
    private lateinit var viewModel: AreaViewModel

    @Before
    fun init() {
        viewModel = AreaViewModel(repository)
    }

    @Test
    fun testSearchWithoutObserver() {
        viewModel.setQuery("foo", 10)
        verify(repository, never()).search(any(), any())
    }

    @Test
    fun testSearchWhenObserved() {
        viewModel.animals.observeForever(mock())
        viewModel.setQuery("foo", 10)
        verify(repository).search("foo", 10)
        reset(repository)
        viewModel.setQuery("bar", 10)
        verify(repository).search("bar", 10)
    }

    @Test
    fun testChangeWhileObserved() {
        viewModel.animals.observeForever(mock())
        viewModel.setQuery("foo", 10)
        viewModel.setQuery("bar", 10)

        verify(repository).search("foo", 10)
        verify(repository).search("bar", 10)
    }

    @Test
    fun testSearchNextPageWithoutQuery() {
        viewModel.animals.observeForever(mock())
        viewModel.loadNextPage()
        verifyNoInteractions(repository)
    }

    @Test
    fun testSearchNextPageWithQuery() {
        viewModel.animals.observeForever(mock())
        viewModel.setQuery("foo", 10)
        verify(repository).search("foo", 10)
        reset(repository)

        viewModel.loadNextPage()
        verify(repository).searchNextPage("foo", 10)
    }

    @Test
    fun testSearchNextPageWhenChangeQuery() {
        val nextPage = MutableLiveData<Resource<Boolean>?>()
        whenever(repository.searchNextPage("foo", 10)).thenReturn(nextPage)

        viewModel.animals.observeForever(mock())
        viewModel.setQuery("foo", 10)
        verify(repository).search("foo", 10)

        viewModel.loadMoreState.observeForever(mock())
        viewModel.loadNextPage()
        verify(repository).searchNextPage("foo", 10)
        assertEquals(nextPage.hasActiveObservers(), true)

        viewModel.setQuery("bar", 10)
        assertEquals(nextPage.hasActiveObservers(), false)
        verify(repository).search("bar", 10)
        verify(repository, never()).searchNextPage("bar", 10)
    }

    @Test
    fun testRetry() {
        viewModel.retry()
        verifyNoInteractions(repository)

        viewModel.setQuery("foo", 10)
        viewModel.retry()
        verifyNoInteractions(repository)

        viewModel.animals.observeForever(mock())
        verify(repository).search("foo", 10)
        reset(repository)

        viewModel.retry()
        verify(repository).search("foo", 10)
    }

    @Test
    fun testBlankQuery() {
        viewModel.animals.observeForever(mock())
        viewModel.setQuery("foo", 10)
        verify(repository).search("foo", 10)
        reset(repository)

        viewModel.setQuery("", 10)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun testResetQuery() {
        val observer = mock<Observer<AreaViewModel.Query>>()
        viewModel.query.observeForever(observer)

        viewModel.setQuery("foo", 10)
        verify(observer).onChanged(AreaViewModel.Query("foo", 10))
        reset(observer)

        viewModel.setQuery("foo", 10)
        verifyNoMoreInteractions(observer)
        viewModel.setQuery("bar", 10)
        verify(observer).onChanged(AreaViewModel.Query("bar", 10))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdate() = runTest {
        val current = TestUtil.createAnimal("foo", "bar")
        val updated = current.copy(bookmark = true)
        viewModel.toggleBookmark(current)
        advanceUntilIdle()
        verify(repository).updateAnimal(updated)
    }

}