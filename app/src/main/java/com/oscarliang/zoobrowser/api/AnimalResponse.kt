package com.oscarliang.zoobrowser.api

import com.google.gson.annotations.SerializedName
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.Area

data class AnimalResponse(
    @SerializedName("result")
    val result: Result
) {
    data class Result(
        @SerializedName("count")
        val count: Int,
        @SerializedName("results")
        val results: List<Animal>
    )
}