package com.oscarliang.zoobrowser.di

import android.app.Application
import androidx.room.Room
import com.oscarliang.zoobrowser.api.ZooService
import com.oscarliang.zoobrowser.db.AnimalDao
import com.oscarliang.zoobrowser.db.AreaDao
import com.oscarliang.zoobrowser.db.ZooDatabase
import com.oscarliang.zoobrowser.util.RateLimiter
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideZooService(): ZooService {
        return Retrofit.Builder()
            .baseUrl("https://data.taipei/api/v1/dataset/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZooService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): ZooDatabase {
        return Room
            .databaseBuilder(app, ZooDatabase::class.java, "zoo.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAreaDao(db: ZooDatabase): AreaDao {
        return db.areaDao()
    }

    @Singleton
    @Provides
    fun provideAnimalDao(db: ZooDatabase): AnimalDao {
        return db.animalDao()
    }

    @Singleton
    @Provides
    fun provideRateLimiter(): RateLimiter<String> {
        return RateLimiter(10, TimeUnit.MINUTES)
    }

}