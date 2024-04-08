package com.oscarliang.zoobrowser.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey
    @SerializedName("_id")
    val id: Int,
    @SerializedName("a_name_ch")
    val name: String,
    @SerializedName("a_location")
    val location: String,
    @SerializedName("a_feature")
    val feature: String,
    @SerializedName("a_pic01_url")
    val imageUrl: String,
    var bookmark: Boolean = false
)