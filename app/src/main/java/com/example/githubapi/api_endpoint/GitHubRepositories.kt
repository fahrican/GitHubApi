package com.example.githubapi.api_endpoint

import com.example.githubapi.model.GitHubRepository
import io.reactivex.Observable
import retrofit2.http.GET

interface GitHubRepositories {

    @GET("repositories")
    fun fetchAllPublicRepositories(): Observable<GitHubRepository>
}