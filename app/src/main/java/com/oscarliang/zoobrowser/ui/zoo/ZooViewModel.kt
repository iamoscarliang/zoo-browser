package com.oscarliang.zoobrowser.ui.zoo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.oscarliang.zoobrowser.model.Area
import com.oscarliang.zoobrowser.repository.AreaRepository
import com.oscarliang.zoobrowser.util.Resource
import javax.inject.Inject

class ZooViewModel @Inject constructor(
    private val repository: AreaRepository
) : ViewModel() {

    private val _loadTrigger = MutableLiveData<Unit>()
    val loadTrigger: LiveData<Unit>
        get() = _loadTrigger

    val areas: LiveData<Resource<List<Area>>> = _loadTrigger.switchMap {
        repository.getAreas()
    }

    fun load() {
        if (_loadTrigger.value == null) {
            _loadTrigger.value = Unit
        }
    }

    fun refresh() {
        _loadTrigger.value?.let {
            _loadTrigger.value = it
        }
    }

}