package com.ihsanmkls.storyapp.data.api

import com.google.gson.annotations.SerializedName

data class User(
    @field:SerializedName("isLogin")
    val isLogin: Boolean,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String
)