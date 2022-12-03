package com.example.headsupgame

import retrofit2.http.GET

interface APIInterface {

    @GET("celebrities/")

    fun getCelebritiesData():retrofit2.Call<Celebrities>

}