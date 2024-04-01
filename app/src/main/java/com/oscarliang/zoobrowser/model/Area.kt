package com.oscarliang.zoobrowser.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "areas")
data class Area(
    @PrimaryKey
    @field:SerializedName("_id")
    val id: Int,
    @field:SerializedName("e_name")
    val name: String,
    @field:SerializedName("e_category")
    val category: String,
    @field:SerializedName("e_info")
    val info: String,
    @field:SerializedName("e_memo")
    val memo: String?,
    @field:SerializedName("e_pic_url")
    val imageUrl: String,
    @field:SerializedName("e_url")
    val url: String
)