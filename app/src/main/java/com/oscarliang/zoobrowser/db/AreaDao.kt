package com.oscarliang.zoobrowser.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oscarliang.zoobrowser.model.Area

@Dao
abstract class AreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAreas(areas: List<Area>)

    @Query("SELECT * FROM areas")
    abstract fun getAreas(): LiveData<List<Area>>

}
