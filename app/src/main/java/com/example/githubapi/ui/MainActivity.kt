package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.githubapi.R
import com.example.githubapi.adapter.RepositoryAdapter
import com.example.githubapi.api_endpoint.GitHubRepositories
import com.example.githubapi.model.Repository
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val BASE_URL by lazy { "https://api.github.com/" }
    private lateinit var gitHubRepositories: GitHubRepositories
    private lateinit var repositoriesList: ArrayList<Repository>
    private lateinit var repositoryAdapter: RepositoryAdapter
    // RxJava related fields
    private lateinit var repositoryObservable: Observable<ArrayList<Repository>>
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Network request
        val retrofit: Retrofit = generateRetrofitGsonBuilder()
        gitHubRepositories = retrofit.create(GitHubRepositories::class.java)
        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        repositoriesList = ArrayList()
        repositoryAdapter = RepositoryAdapter(repositoriesList)
        compositeDisposable = CompositeDisposable()
        recycler_view.setHasFixedSize(true)
        recycler_view.setItemViewCacheSize(25)
        /*TODO: BASE_URL and generateRetrofitGsonBuilder() in separate class for all Activities
         * extract styles from views of activity_repository_detail.xml
         */
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = repositoryAdapter
    }

    override fun onStart() {
        super.onStart()
        fetchForRepositories()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onRefresh() {
        fetchForRepositories()
    }

    private fun generateRetrofitGsonBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun fetchForRepositories() {
        swipe_refresh.isRefreshing = true
        repositoryObservable = gitHubRepositories.fetchAllPublicRepositories()
        subscribeObservableOfRepository()
    }

    private fun subscribeObservableOfRepository() {
        repositoriesList.clear()
        compositeDisposable.add(
            repositoryObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(createRepositoryObserver())
        )
    }

    private fun createRepositoryObserver(): DisposableObserver<ArrayList<Repository>> {
        return object : DisposableObserver<ArrayList<Repository>>() {
            override fun onNext(repos: ArrayList<Repository>) {
                if (!repositoriesList.equals(repos)) {
                    repositoriesList = repos
                }
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createRepoObserver", "Repository error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView() {
        if (repositoriesList.size > 0) {
            swipe_refresh.isRefreshing = false
            empty_text.visibility = View.GONE
            retry_fetch_button.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            repositoryAdapter.setRepositories(repositoriesList)
        } else {
            recycler_view.visibility = View.GONE
            empty_text.visibility = View.VISIBLE
            retry_fetch_button.visibility = View.VISIBLE
            retry_fetch_button.setOnClickListener { fetchForRepositories() }
        }
    }
}
