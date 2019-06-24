package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.githubapi.R
import com.example.githubapi.adapter.RepositoryAdapter
import com.example.githubapi.api_endpoint.GitHubRepositories
import com.example.githubapi.model.Repository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private val ENDPOINT_URL by lazy { "https://api.github.com/" }
    private lateinit var gitHubRepositories: GitHubRepositories
    private lateinit var repositoryAdapter: RepositoryAdapter
    private lateinit var repositoriesList: ArrayList<Repository>
    // RxJava related fields
    private lateinit var repositoryObservable: Observable<Repository>
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
