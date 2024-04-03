package com.oscarliang.zoobrowser.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map

abstract class NetworkBoundResource<T> {

    fun asLiveData() = liveData<Resource<T>> {
        emit(Resource.loading(null))

        if (shouldFetch(query())) {
            val disposable = emitSource(queryObservable().map { Resource.loading(it) })

            try {
                val fetchedData = fetch()
                // Stop the previous emission to avoid dispatching the saveFetchResult as `Resource.loading`
                disposable.dispose()
                saveFetchResult(fetchedData)
                // Re-establish the emission as `Resource.success`
                emitSource(queryObservable().map { Resource.success(it) })
            } catch (e: Exception) {
                onFetchFailed(e)
                emitSource(queryObservable().map {
                    Resource.error(
                        e.message ?: "Unknown error",
                        it
                    )
                })
            }
        } else {
            emitSource(queryObservable().map { Resource.success(it) })
        }
    }

    abstract suspend fun query(): T
    abstract fun queryObservable(): LiveData<T>
    abstract suspend fun fetch(): T
    abstract suspend fun saveFetchResult(data: T)
    open fun onFetchFailed(exception: Exception) = Unit
    open fun shouldFetch(data: T) = true
}