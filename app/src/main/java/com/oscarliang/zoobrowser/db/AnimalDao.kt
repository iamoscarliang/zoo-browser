package com.oscarliang.zoobrowser.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oscarliang.zoobrowser.model.Animal

@Dao
abstract class AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAnimals(animals: List<Animal>)

    @Query("SELECT * FROM animals WHERE location = :query")
    abstract fun getAnimals(query: String): LiveData<List<Animal>>

    @Query("SELECT * FROM animals WHERE id = :id")
    abstract fun getAnimalById(id: Int): LiveData<Animal>

    @Query("SELECT * FROM animals WHERE bookmark = 1")
    abstract fun getBookmarks(): LiveData<List<Animal>>

    @Update
    abstract fun updateAnimal(animal: Animal)

}
