package com.oscarliang.zoobrowser.ui.animal

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.repository.AnimalRepository
import com.oscarliang.zoobrowser.util.MainDispatcherRule
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
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class AnimalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock<AnimalRepository>()
    private lateinit var viewModel: AnimalViewModel

    @Before
    fun init() {
        viewModel = AnimalViewModel(repository)
    }

    @Test
    fun testLoadWithoutObserver() {
        viewModel.setAnimalId(1)
        verify(repository, never()).getAnimalById(any())
    }

    @Test
    fun testLoadWhenObserved() {
        viewModel.animal.observeForever(mock())
        viewModel.setAnimalId(1)
        verify(repository).getAnimalById(1)
        reset(repository)
        viewModel.setAnimalId(2)
        verify(repository).getAnimalById(2)
    }

    @Test
    fun testSendResultToUI() {
        val foo = MutableLiveData<Animal>()
        val bar = MutableLiveData<Animal>()
        whenever(repository.getAnimalById(1)).thenReturn(foo)
        whenever(repository.getAnimalById(2)).thenReturn(bar)

        val observer = mock<Observer<Animal>>()
        viewModel.animal.observeForever(observer)
        viewModel.setAnimalId(1)
        verify(observer, never()).onChanged(any())

        val fooAnimal = TestUtil.createAnimal("foo", "fea")
        foo.value = fooAnimal
        verify(observer).onChanged(fooAnimal)
        reset(observer)

        val barAnimal = TestUtil.createAnimal("bar", "fea")
        bar.value = barAnimal
        viewModel.setAnimalId(2)
        verify(observer).onChanged(barAnimal)
    }

    @Test
    fun testNullId() {
        viewModel.setAnimalId(1)
        viewModel.setAnimalId(null)
        val observer = mock<Observer<Animal?>>()
        viewModel.animal.observeForever(observer)
        verify(observer).onChanged(null)
    }

    @Test
    fun testResetId() {
        val observer = mock<Observer<Int?>>()
        viewModel.id.observeForever(observer)

        viewModel.setAnimalId(1)
        verify(observer).onChanged(1)
        reset(observer)

        viewModel.setAnimalId(1)
        verifyNoMoreInteractions(observer)
        viewModel.setAnimalId(2)
        verify(observer).onChanged(2)
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