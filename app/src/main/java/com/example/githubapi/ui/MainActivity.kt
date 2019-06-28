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

    private lateinit var layoutManager: RecyclerView.LayoutManager
    var visibleItemCount = 0
    var pastVisibleItemCount = 0
    var loading = false
    var pageId = 1
    var totalItemCount = 0
    private val PAGE_SIZE = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Declare class fields
        repositoriesList = ArrayList()
        compositeDisposable = CompositeDisposable()
        //Set properties for RecyclerView to display list of repos
        layoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.setHasFixedSize(true)
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
        progressBar.visibility = View.VISIBLE
        val gitHubRepositories: GitHubRepositories = RetrofitInstance.getEndPoint()
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
        var list = ArrayList<Repository>()
        return object : DisposableObserver<ArrayList<Repository>>() {
            override fun onNext(repos: ArrayList<Repository>) {
                    list = repos
            }

            override fun onComplete() {
                showArticlesOnRecyclerView(list)
            }

            override fun onError(e: Throwable) {
                Log.e("createRepoObserver", "Repository error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView(repos: ArrayList<Repository>) {
        //Loading circle should disappear before list of repos is shown

        if (repos.size > 0) {

            progressBar.visibility = View.GONE
            loading = true
            setUpAdapter(repos)


            mainEmptyText.visibility = View.GONE
            mainretryFetchButton.visibility = View.GONE
            mainRecyclerView.visibility = View.VISIBLE
        } else {
            mainRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            mainEmptyText.visibility = View.VISIBLE
            mainretryFetchButton.visibility = View.VISIBLE
            mainretryFetchButton.setOnClickListener { fetchForRepositories() }
        }
    }

    private fun setUpAdapter(repos: ArrayList<Repository>) {
        if (repositoriesList.size == 0){
            repositoriesList = repos
            repositoryAdapter = RepositoryAdapter(repositoriesList)
            mainRecyclerView.adapter = repositoryAdapter
        }
        else {
            var currentPosition = (mainRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            repositoriesList.addAll(repos)
            repositoryAdapter.notifyDataSetChanged()
            mainRecyclerView.scrollToPosition(currentPosition)
        }
        mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0){
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItemCount = (mainRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (loading){
                        if ((visibleItemCount + pastVisibleItemCount) >= 8){
                            loading = false
                            pageId++
                            fetchForRepositories()
                        }
                    }
                }
            }

        })
    }
}
