package com.oscarliang.zoobrowser.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.oscarliang.zoobrowser.db.ZooTypeConverters

@Entity(tableName = "animal_search_results")
@TypeConverters(ZooTypeConverters::class)
data class AnimalSearchResult(
    @PrimaryKey
    val query: String,
    val count: Int,
    val animalIds: List<Int>
)