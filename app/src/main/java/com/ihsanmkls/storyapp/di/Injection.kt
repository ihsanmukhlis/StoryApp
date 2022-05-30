package com.ihsanmkls.storyapp.di

import android.content.Context
import com.ihsanmkls.storyapp.api.ApiConfig
import com.ihsanmkls.storyapp.data.repository.StoryRepository
import com.ihsanmkls.storyapp.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}