package com.ihsanmkls.storyapp.api

import com.ihsanmkls.storyapp.data.api.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun userRegister(@Body register: Register): Call<GeneralResponse>

    @POST("login")
    fun userLogin(@Body login: Login): Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page : Int? = null,
        @Query("size") size : Int? = null
    ): StoryResponse

    @GET("stories")
    fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Part file: MultipartBody.Part
    ): Call<GeneralResponse>

}