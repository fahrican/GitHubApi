package com.example.githubapi.api_endpoint

import com.example.githubapi.model.Contributor
import com.example.githubapi.model.Repository
import com.example.githubapi.model.RepositoryDetail
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubRepositories {

    @GET("repositories")
    fun fetchAllPublicRepositories(): Observable<ArrayList<Repository>>

    @GET("repos/{login}/{name}")
    fun fetchRepositoryDetails(
        @Path("login") login: String,
        @Path("name") name: String
    ): Observable<RepositoryDetail>

    @GET("repos/{login}/{name}/contributors")
    fun fetchContributorsList(
        @Path("login") login: String,
        @Path("name") name: String
    ): Observable<ArrayList<Contributor>>
}