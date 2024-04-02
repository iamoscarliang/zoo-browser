package com.oscarliang.zoobrowser.db

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oscarliang.zoobrowser.model.Animal
import com.oscarliang.zoobrowser.model.AnimalSearchResult

@Dao
interface AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<Animal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimalSearchResults(result: AnimalSearchResult)

    @Query("SELECT * FROM animals WHERE location = :query")
    fun getAnimals(query: String): LiveData<List<Animal>>

    @Query("SELECT * FROM animal_search_results WHERE `query` = :query")
    abstract fun getAnimalSearchResult(query: String): AnimalSearchResult?

    fun getOrdered(ids: List<Int>): LiveData<List<Animal>> {
        val order = SparseIntArray()
        ids.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return getAnimalsById(ids).map { animals ->
            animals.sortedWith(compareBy { order.get(it.id) })
        }
    }

    @Query("SELECT * FROM animals WHERE id in (:ids)")
    fun getAnimalsById(ids: List<Int>): LiveData<List<Animal>>

    @Query("SELECT * FROM animals WHERE id = :id")
    fun getAnimalById(id: Int): LiveData<Animal>

    @Query("SELECT * FROM animals WHERE bookmark = 1")
    fun getBookmarks(): LiveData<List<Animal>>

    @Update
    suspend fun updateAnimal(animal: Animal)

}
