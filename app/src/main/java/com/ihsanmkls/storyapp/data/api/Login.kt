package com.ihsanmkls.storyapp.data.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Login(
    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("password")
    var password: String? = null
) : Parcelable
