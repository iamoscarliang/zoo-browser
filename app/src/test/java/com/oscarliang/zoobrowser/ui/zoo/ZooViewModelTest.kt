package com.oscarliang.zoobrowser.ui.zoo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.oscarliang.zoobrowser.repository.AreaRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

@RunWith(JUnit4::class)
class ZooViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock<AreaRepository>()
    private lateinit var viewModel: ZooViewModel

    @Before
    fun init() {
        viewModel = ZooViewModel(repository)
    }

    @Test
    fun testLoadWithoutObserver() {
        viewModel.load()
        verify(repository, never()).getAreas()
    }

    @Test
    fun testLoadWhenObserved() {
        viewModel.areas.observeForever(mock())
        viewModel.load()
        verify(repository).getAreas()
    }

    @Test
    fun testRefresh() {
        viewModel.refresh()
        verifyNoInteractions(repository)

        viewModel.load()
        viewModel.refresh()
        verifyNoInteractions(repository)

        viewModel.areas.observeForever(mock())
        verify(repository).getAreas()
        reset(repository)

        viewModel.refresh()
        verify(repository).getAreas()
    }

}