package com.oscarliang.zoobrowser.api

import com.google.gson.annotations.SerializedName
import com.oscarliang.zoobrowser.model.Area

data class AreaResponse(
    @field:SerializedName("result")
    val result: Result
)

data class Result(
    @field:SerializedName("results")
    val results: List<Area>
)