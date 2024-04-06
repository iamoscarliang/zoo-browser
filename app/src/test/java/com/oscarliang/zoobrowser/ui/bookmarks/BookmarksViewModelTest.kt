package com.oscarliang.zoobrowser.ui.bookmarks

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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class BookmarksViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mock<AnimalRepository>()
    private lateinit var viewModel: BookmarksViewModel

    @Before
    fun init() {
        viewModel = BookmarksViewModel(repository)
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