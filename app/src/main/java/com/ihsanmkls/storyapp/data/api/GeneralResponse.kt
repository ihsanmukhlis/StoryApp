package com.ihsanmkls.storyapp.data.api

import com.google.gson.annotations.SerializedName

data class GeneralResponse (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)