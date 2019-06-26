package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.githubapi.R
import com.example.githubapi.adapter.ContributorAdapter
import com.example.githubapi.api_endpoint.GitHubRepositories
import com.example.githubapi.model.Contributor
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class ContributorActivity : AppCompatActivity() {

    private val BASE_URL by lazy { "https://api.github.com/" }
    private lateinit var gitHubRepositories: GitHubRepositories
    private lateinit var contributorsList: ArrayList<Contributor>
    private lateinit var contributorAdapter: ContributorAdapter
    // RxJava related fields
    private lateinit var repositoryObservable: Observable<ArrayList<Contributor>>
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contributor)

        supportActionBar?.title = "List of all Contributors"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
