package com.oscarliang.zoobrowser.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ZooService {

    @GET("5a0e5fbb-72f8-41c6-908e-2fb25eff9b8a?scope=resourceAquire")
    suspend fun getAreas(): AreaResponse

    @GET("a3e2b221-75e0-45c1-8f97-75acbd43d613?scope=resourceAquire")
    suspend fun getAnimals(
        @Query("q") query: String,
        @Query("limit") limit: Int
    ): AnimalResponse

    @GET("a3e2b221-75e0-45c1-8f97-75acbd43d613?scope=resourceAquire")
    suspend fun getAnimals(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): AnimalResponse

}