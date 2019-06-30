package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.githubapi.R
import com.example.githubapi.adapter.RepositoryAdapter
import com.example.githubapi.api_endpoint.GitHubRepositories
import com.example.githubapi.api_endpoint.RetrofitInstance
import com.example.githubapi.model.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var repositoriesList: ArrayList<Repository>
    private lateinit var repositoryAdapter: RepositoryAdapter
    // RxJava related fields
    private lateinit var repositoryObservable: Observable<ArrayList<Repository>>
    private lateinit var compositeDisposable: CompositeDisposable
    //Items for OnScrollListener()
    private lateinit var fieldLayoutManager: RecyclerView.LayoutManager
    var visibleItemCount = 0
    var pastVisibleItemCount = 0
    var loading = false
    var pageId = 1
    var totalItemCount = 0
    private val PAGE_SIZE = 25


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Declare class fields
        repositoriesList = ArrayList()
        repositoryAdapter = RepositoryAdapter(repositoriesList)
        compositeDisposable = CompositeDisposable()
        //Set properties for RecyclerView to display list of repos
        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.setItemViewCacheSize(25)
        fieldLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = fieldLayoutManager
        mainRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onStart() {
        super.onStart()
        fetchForRepositories()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


    private fun fetchForRepositories() {
        val gitHubRepositories: GitHubRepositories = RetrofitInstance.getEndPoint()
        repositoryObservable = gitHubRepositories.fetchAllPublicRepositories()
        subscribeObservableOfRepository()
    }

    private fun subscribeObservableOfRepository() {
        compositeDisposable.add(
            repositoryObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(createRepositoryObserver())
        )
    }

    private fun createRepositoryObserver(): DisposableObserver<ArrayList<Repository>> {
        return object : DisposableObserver<ArrayList<Repository>>() {
            override fun onNext(repos: ArrayList<Repository>) {
                repos.forEach {
                    if (!repositoriesList.contains(it)) {
                        repositoriesList.add(it)
                    }
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
        //Loading circle should disappear before list of repos is shown

        if (repositoriesList.size > 0) {
            repositoryAdapter.setRepositories(repositoriesList)
            mainRecyclerView.adapter = repositoryAdapter
            mainEmptyText.visibility = View.GONE
            mainretryFetchButton.visibility = View.GONE
            mainRecyclerView.visibility = View.VISIBLE
            loading = true
            mainRecyclerView.addOnScrollListener(checkNumberOfScrolledItems())
        } else {
            mainRecyclerView.visibility = View.GONE
            mainEmptyText.visibility = View.VISIBLE
            mainretryFetchButton.visibility = View.VISIBLE
            mainretryFetchButton.setOnClickListener { fetchForRepositories() }
        }
    }

    private fun checkNumberOfScrolledItems(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    progressBar.visibility = View.VISIBLE
                    visibleItemCount = fieldLayoutManager.childCount
                    totalItemCount = fieldLayoutManager.itemCount
                    pastVisibleItemCount =
                        (mainRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItemCount) >= 8 && totalItemCount >= PAGE_SIZE) {
                            loading = false
                            pageId++
                            Log.v("page", "${pageId}")
                            progressBar.visibility = View.GONE
                            Log.v("before crash", "${pageId}")
                            fetchForRepositories()
                            Log.v("after crash", "${pageId}")
                        }
                    } else {
                        loading = false
                    }
                }
            }
        }
    }
}
