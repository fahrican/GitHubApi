package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
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
        fullNameFromIntent = intent.getStringExtra(RepositoryAdapter.FULL_NAME)
        //Network request
        val retrofit: Retrofit = generateRetrofitGsonBuilder()
        containsLoginAndName = ArrayList()
        //Get {login}/{repository_name}
        containsLoginAndName = fullNameFromIntent.split("/")
        gitHubRepositories = retrofit.create(GitHubRepositories::class.java)
        contributorsList = ArrayList()
        contributorAdapter = ContributorAdapter(contributorsList)
        compositeDisposable = CompositeDisposable()
        //list of contributors
        contributorRecyclerView.setHasFixedSize(true)
        contributorRecyclerView.layoutManager = GridLayoutManager(this, 2)
        contributorRecyclerView.itemAnimator = DefaultItemAnimator()
        contributorRecyclerView.adapter = contributorAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        fetchForContributors()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun fetchForContributors() {
        repositoryObservable =
            gitHubRepositories.fetchContributorsList(containsLoginAndName.get(0), containsLoginAndName.get(1))
        subscribeObservableOfContributor()
    }

    private fun subscribeObservableOfContributor() {
        contributorsList.clear()
        compositeDisposable.add(
            repositoryObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(createContributorObserver())
        )
    }

    private fun createContributorObserver(): DisposableObserver<ArrayList<Contributor>> {
        return object : DisposableObserver<ArrayList<Contributor>>() {

            override fun onNext(contributors: ArrayList<Contributor>) {
                if (!contributorsList.equals(contributors)) {
                    contributorsList = contributors
                }
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }


            override fun onError(e: Throwable) {
                Log.e("createContributorObserv", "Contributor error: ${e.message}")
            }

        }
    }

    private fun showArticlesOnRecyclerView() {
        if (contributorsList.size > 0) {
            contributorRecyclerView.visibility = View.VISIBLE
            contributorAdapter.setContributors(contributorsList)
        } else {
            contributorRecyclerView.visibility = View.GONE
        }
    }

    private fun generateRetrofitGsonBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}
