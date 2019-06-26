package com.example.githubapi.api_endpoint

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    var retrofit: Retrofit? = null
    private val BASE_URL by lazy { "https://api.github.com/" }

    fun getEndPoint(): GitHubRepositories {

        if (retrofit == null) {

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofit!!.create(GitHubRepositories::class.java)
    }
}