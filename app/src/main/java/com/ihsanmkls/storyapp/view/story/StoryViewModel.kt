package com.ihsanmkls.storyapp.view.story

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ihsanmkls.storyapp.data.api.Story
import com.ihsanmkls.storyapp.data.repository.StoryRepository
import com.ihsanmkls.storyapp.di.Injection

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<Story>> = storyRepository.getStory(token).cachedIn(viewModelScope)

    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(StoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoryViewModel(Injection.provideRepository(context)) as T
            }
            else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}