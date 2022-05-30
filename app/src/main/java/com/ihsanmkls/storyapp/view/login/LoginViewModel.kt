package com.ihsanmkls.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihsanmkls.storyapp.data.UserPreferences
import com.ihsanmkls.storyapp.data.api.User
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreferences) : ViewModel() {

    fun setUser(user: User) {
        viewModelScope.launch {
            pref.saveUser(User(user.isLogin, user.userId, user.name, user.token))
        }
    }
}