package com.oscarliang.zoobrowser.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oscarliang.zoobrowser.model.Area

@Dao
interface AreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAreas(areas: List<Area>)

    @Query("SELECT * FROM areas")
    fun getAreas(): List<Area>

    @Query("SELECT * FROM areas")
    fun getObservableAreas(): LiveData<List<Area>>

}
