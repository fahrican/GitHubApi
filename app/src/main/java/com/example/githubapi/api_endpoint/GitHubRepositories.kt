package com.example.githubapi.api_endpoint

import com.example.githubapi.model.Repository
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GitHubRepositories {
/*
    @GET()
    fun fetchAllPublicRepositories(@Url url: String): Observable<List<Repository>>*/

    @GET("repositories")
    fun fetchAllPublicRepositories(): Call<List<Repository>>
}