package com.ihsanmkls.storyapp.view.main

import androidx.lifecycle.*
import com.ihsanmkls.storyapp.data.UserPreferences
import com.ihsanmkls.storyapp.data.api.User
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}