package com.oscarliang.zoobrowser.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "areas")
data class Area(
    @PrimaryKey
    @SerializedName("_id")
    val id: Int,
    @SerializedName("e_name")
    val name: String,
    @SerializedName("e_category")
    val category: String,
    @SerializedName("e_info")
    val info: String,
    @SerializedName("e_memo")
    val memo: String?,
    @SerializedName("e_pic_url")
    val imageUrl: String,
    @SerializedName("e_url")
    val url: String
) : Parcelable