package com.oscarliang.zoobrowser.ui.area

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.repository.AnimalRepository
import com.oscarliang.zoobrowser.util.AbsentLiveData
import com.oscarliang.zoobrowser.util.Resource
import com.oscarliang.zoobrowser.util.State
import kotlinx.coroutines.launch
import javax.inject.Inject

class AreaViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    private val _query = MutableLiveData<Query>()
    val query: LiveData<Query>
        get() = _query

    val animals: LiveData<Resource<List<Animal>>> = _query.switchMap { input ->
        input.ifExists { query, limit ->
            repository.search(query, limit)
        }
    }

    private val nextPageHandler = NextPageHandler(repository)
    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun setQuery(query: String, limit: Int) {
        val update = Query(query, limit)
        if (_query.value == update) {
            return
        }
        nextPageHandler.reset()
        _query.value = update
    }

    fun loadNextPage() {
        _query.value?.let {
            if (it.query.isNotBlank()) {
                nextPageHandler.queryNextPage(it.query, it.limit)
            }
        }
    }

    fun retry() {
        _query.value?.let {
            _query.value = it
        }
    }

    fun toggleBookmark(animal: Animal) {
        val current = animal.bookmark
        val updated = animal.copy(bookmark = !current)
        viewModelScope.launch {
            repository.updateAnimal(updated)
        }
    }

    data class Query(
        val query: String,
        val limit: Int
    ) {
        fun <T> ifExists(f: (String, Int) -> LiveData<T>): LiveData<T> {
            return if (query.isBlank() || limit == 0) {
                AbsentLiveData.create()
            } else {
                f(query, limit)
            }
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(
        private val repository: AnimalRepository
    ) : Observer<Resource<Boolean>?> {

        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var nextPageLiveData: LiveData<Resource<Boolean>?>? = null
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(
            query: String,
            limit: Int
        ) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query, limit)
            loadMoreState.value = LoadMoreState(
                isRunning = true,
                errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(value: Resource<Boolean>?) {
            if (value == null) {
                reset()
            } else {
                when (value.state) {
                    State.SUCCESS -> {
                        _hasMore = value.data == true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                    }

                    State.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = value.message
                            )
                        )
                    }

                    State.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                isRunning = false,
                errorMessage = null
            )
        }
    }

}