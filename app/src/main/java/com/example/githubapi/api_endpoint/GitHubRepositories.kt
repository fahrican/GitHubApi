package com.example.githubapi.api_endpoint

import com.example.githubapi.model.Repository
import io.reactivex.Observable
import retrofit2.http.GET

interface GitHubRepositories {

    @GET("repositories")
    fun fetchAllPublicRepositories(): Observable<ArrayList<Repository>>

  /*  @GET("repositories")
    fun fetchAllPublicRepositories(): Call<List<Repository>>*/
}