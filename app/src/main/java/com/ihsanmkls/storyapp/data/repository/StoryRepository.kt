package com.ihsanmkls.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.ihsanmkls.storyapp.api.ApiService
import com.ihsanmkls.storyapp.data.api.Story
import com.ihsanmkls.storyapp.data.remotemediator.StoryRemoteMediator
import com.ihsanmkls.storyapp.database.StoryDatabase

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}