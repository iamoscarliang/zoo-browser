package com.oscarliang.zoobrowser.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.Area

@Database(
    entities = [Area::class, Animal::class],
    version = 1
)
abstract class ZooDatabase : RoomDatabase() {

    abstract fun areaDao(): AreaDao

    abstract fun animalDao(): AnimalDao

}