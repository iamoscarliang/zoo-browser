package com.oscarliang.zoobrowser.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oscarliang.zoobrowser.model.Animal

@Dao
interface AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<Animal>)

    @Query("SELECT * FROM animals WHERE location = :query")
    fun getAnimals(query: String): LiveData<List<Animal>>

    @Query("SELECT * FROM animals WHERE id = :id")
    fun getAnimalById(id: Int): LiveData<Animal>

    @Query("SELECT * FROM animals WHERE bookmark = 1")
    fun getBookmarks(): LiveData<List<Animal>>

    @Update
    suspend fun updateAnimal(animal: Animal)

}
