package com.oscarliang.zoobrowser.ui.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.repository.AnimalRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarksViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    val bookmarks: LiveData<List<Animal>> = repository.getBookmarks()

    fun toggleBookmark(animal: Animal) {
        val current = animal.bookmark
        val updated = animal.copy(bookmark = !current)
        viewModelScope.launch {
            repository.updateAnimal(updated)
        }
    }

}