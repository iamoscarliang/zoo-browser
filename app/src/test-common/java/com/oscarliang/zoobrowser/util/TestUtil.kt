package com.oscarliang.zoobrowser.util

import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.Area

private const val UNKNOWN_ID = -1

object TestUtil {

    fun createAreas(
        count: Int,
        name: String,
        info: String
    ): List<Area> {
        return (0 until count).map {
            createArea(
                id = it,
                name = name + it,
                info = info + it
            )
        }
    }

    fun createArea(
        name: String,
        info: String
    ) = createArea(
        id = UNKNOWN_ID,
        name = name,
        info = info
    )

    fun createArea(
        id: Int,
        name: String,
        info: String
    ) = Area(
        id = id,
        name = name,
        category = "",
        info = info,
        memo = "",
        imageUrl = "",
        url = ""
    )

    fun createAnimals(
        count: Int,
        name: String,
        feature: String
    ): List<Animal> {
        return (0 until count).map {
            createAnimal(
                id = it,
                name = name + it,
                feature = feature + it
            )
        }
    }

    fun createAnimal(
        name: String,
        feature: String
    ) = createAnimal(
        id = UNKNOWN_ID,
        name = name,
        feature = feature
    )

    fun createAnimal(
        id: Int,
        name: String,
        feature: String
    ) = Animal(
        id = id,
        name = name,
        location = "",
        feature = feature,
        imageUrl = "",
        bookmark = false
    )

}