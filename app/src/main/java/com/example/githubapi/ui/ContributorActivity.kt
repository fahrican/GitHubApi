package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.githubapi.R
import com.example.githubapi.adapter.ContributorAdapter
import com.example.githubapi.adapter.RepositoryAdapter
import com.example.githubapi.api_endpoint.GitHubRepositories
import com.example.githubapi.model.Contributor
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contributor.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ContributorActivity : AppCompatActivity() {

    private val BASE_URL by lazy { "https://api.github.com/" }
    private lateinit var gitHubRepositories: GitHubRepositories
    private lateinit var contributorsList: ArrayList<Contributor>
    private lateinit var contributorAdapter: ContributorAdapter
    private lateinit var fullNameFromIntent: String
    private lateinit var containsLoginAndName: List<String>
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
