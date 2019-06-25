package com.example.githubapi.api_endpoint

import com.example.githubapi.model.Repository
import com.example.githubapi.model.RepositoryDetail
import io.reactivex.Observable
import retrofit2.http.GET

interface GitHubRepositories {

    @GET("repositories")
    fun fetchAllPublicRepositories(): Observable<ArrayList<Repository>>

    @GET("repos")
    fun fetchRepositoryDetails(): Observable<RepositoryDetail>
}