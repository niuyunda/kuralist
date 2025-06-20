package com.kuralist.app.core.di

import android.content.Context
import androidx.room.Room
import com.kuralist.app.core.services.database.SchoolDao
import com.kuralist.app.core.services.database.SchoolDatabase
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.android.qualifiers.ApplicationContext
// import dagger.hilt.components.SingletonComponent
// import javax.inject.Singleton

// @Module
// @InstallIn(SingletonComponent::class)
object DatabaseModule {

    // @Provides
    // @Singleton
    fun provideSchoolDatabase(context: Context): SchoolDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SchoolDatabase::class.java,
            "school_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // @Provides
    fun provideSchoolDao(database: SchoolDatabase): SchoolDao {
        return database.schoolDao()
    }
} 