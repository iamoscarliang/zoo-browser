package com.oscarliang.zoobrowser.api

import com.google.gson.annotations.SerializedName
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.Area

data class AnimalResponse(
    @field:SerializedName("result")
    val result: Result
) {
    data class Result(
        @field:SerializedName("results")
        val results: List<Animal>
    )
}