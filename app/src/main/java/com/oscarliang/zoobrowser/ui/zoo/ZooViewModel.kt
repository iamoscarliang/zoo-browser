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

    private val _loadTrigger = MutableLiveData(Unit)
    val loadTrigger: LiveData<Unit>
        get() = _loadTrigger

    val areas: LiveData<Resource<List<Area>>> = _loadTrigger.switchMap {
        repository.getAreas()
    }

    init {
        refresh()
    }

    fun refresh() {
        _loadTrigger.value = Unit
    }

}