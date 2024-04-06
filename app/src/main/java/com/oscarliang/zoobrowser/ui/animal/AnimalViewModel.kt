package com.oscarliang.zoobrowser.ui.animal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.repository.AnimalRepository
import com.oscarliang.zoobrowser.util.AbsentLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnimalViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    private val _id = MutableLiveData<Int?>()
    val id: LiveData<Int?>
        get() = _id

    val animal: LiveData<Animal> = _id.switchMap { id ->
        if (id == null) {
            AbsentLiveData.create()
        } else {
            repository.getAnimalById(id)
        }
    }

    fun setAnimalId(id: Int?) {
        if (_id.value != id) {
            _id.value = id
        }
    }

    fun toggleBookmark(animal: Animal) {
        val current = animal.bookmark
        val updated = animal.copy(bookmark = !current)
        viewModelScope.launch {
            repository.updateAnimal(updated)
        }
    }

}