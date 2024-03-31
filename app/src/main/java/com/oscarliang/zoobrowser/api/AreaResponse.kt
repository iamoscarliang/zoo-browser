package com.oscarliang.zoobrowser.api

import com.google.gson.annotations.SerializedName
import com.oscarliang.zoobrowser.model.Area

data class AreaResponse(
    @SerializedName("results")
    val results: List<Area>
)