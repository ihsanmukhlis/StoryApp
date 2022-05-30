package com.ihsanmkls.storyapp.data.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ihsanmkls.storyapp.BuildConfig
import com.ihsanmkls.storyapp.api.ApiConfig
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class Story(
    @PrimaryKey
    @field:SerializedName("id")
    var id: String,

    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("description")
    var description: String,

    @field:SerializedName("photoUrl")
    var photoUrl: String,

    @field:SerializedName("createdAt")
    var createdAt: String,

    @field:SerializedName("lat")
    var lat: Double,

    @field:SerializedName("lon")
    var lon: Double
) : Parcelable
