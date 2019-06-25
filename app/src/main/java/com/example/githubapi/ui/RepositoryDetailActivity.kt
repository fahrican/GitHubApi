package com.example.githubapi.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.githubapi.R
import com.example.githubapi.adapter.RepositoryAdapter
import com.example.githubapi.api_endpoint.GitHubRepositories
import com.example.githubapi.model.RepositoryDetail
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_repository_detail.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryDetailActivity : AppCompatActivity() {

    private val BASE_URL by lazy { "https://api.github.com/" }
    private lateinit var gitHubRepositories: GitHubRepositories
    private var repositoryDetail: RepositoryDetail? = null
    private lateinit var fullNameFromIntent: String
    private lateinit var containsLoginAndName: List<String>
    // RxJava related fields
    private lateinit var repositoryDetailsObservable: Observable<RepositoryDetail>
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_detail)

        fullNameFromIntent = intent.getStringExtra(RepositoryAdapter.FULL_NAME)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = fullNameFromIntent
        containsLoginAndName = ArrayList()
        containsLoginAndName = fullNameFromIntent.split("/")
        val retrofit: Retrofit = generateRetrofitGsonBuilder()
        gitHubRepositories = retrofit.create(GitHubRepositories::class.java)
        compositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
        super.onStart()
        fetchForRepositoryDetails()
        contributorsButton.setOnClickListener {
            setIntentForContributors()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun generateRetrofitGsonBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun fetchForRepositoryDetails() {
        repositoryDetailsObservable =
            gitHubRepositories.fetchRepositoryDetails(containsLoginAndName.get(0), containsLoginAndName.get(1))
        subscribeObservableRepositoryDetails()
    }

    private fun subscribeObservableRepositoryDetails() {
        repositoryDetail = null
        compositeDisposable.add(
            repositoryDetailsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(createRepositoryDetailObserver())
        )
    }

    private fun createRepositoryDetailObserver(): DisposableObserver<RepositoryDetail> {
        return object : DisposableObserver<RepositoryDetail>() {

            override fun onNext(repoDetail: RepositoryDetail) {
                repositoryDetail = repoDetail
            }

            override fun onComplete() {
                setAttributesForRepositoryDetail()
            }

            override fun onError(e: Throwable) {
                Log.e("createRepoDetailObserv", "RepositoryDetail error: ${e.message}")

            }
        }
    }

    private fun setAttributesForRepositoryDetail() {
        sizeValue.text = repositoryDetail?.size.toString()
        stargazersCountValue.text = repositoryDetail?.stargazers_count.toString()
        forksCountValue.text = repositoryDetail?.forks_count.toString()
    }

    private fun setIntentForContributors() {
        val contributorsIntent = Intent(this, ContributorActivity::class.java)
        contributorsIntent.putExtra(RepositoryAdapter.FULL_NAME, fullNameFromIntent)
        startActivity(contributorsIntent)
    }
}
