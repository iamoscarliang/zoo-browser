package com.oscarliang.zoobrowser.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ZooService {

    @GET("5a0e5fbb-72f8-41c6-908e-2fb25eff9b8a?scope=resourceAquire")
    fun getAreas(): LiveData<ApiResponse<AreaResponse>>

    @GET("a3e2b221-75e0-45c1-8f97-75acbd43d613?scope=resourceAquire")
    fun getAnimals(
        @Query("q") query: String,
        @Query("limit") limit: Int
    ): LiveData<ApiResponse<AnimalResponse>>

    @GET("a3e2b221-75e0-45c1-8f97-75acbd43d613?scope=resourceAquire")
    fun getAnimals(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Call<AnimalResponse>

}