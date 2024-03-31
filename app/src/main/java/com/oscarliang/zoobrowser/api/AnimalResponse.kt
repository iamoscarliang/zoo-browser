package com.oscarliang.zoobrowser.api

import com.google.gson.annotations.SerializedName
import com.oscarliang.zoobrowser.model.Animal

data class AnimalResponse(
    @SerializedName("results")
    val results: List<Animal>
)