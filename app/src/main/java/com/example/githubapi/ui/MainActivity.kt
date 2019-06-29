package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var repositoriesList: ArrayList<Repository>
    private lateinit var repositoryAdapter: RepositoryAdapter
    // RxJava related fields
    private lateinit var repositoryObservable: Observable<ArrayList<Repository>>
    private lateinit var compositeDisposable: CompositeDisposable
    //Items for OnScrollListener()
    private lateinit var layoutManager: RecyclerView.LayoutManager
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
        compositeDisposable = CompositeDisposable()
        //Set properties for RecyclerView to display list of repos
        layoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        fetchAllPublicRepositories()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
/*
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
*/

    private fun fetchAllPublicRepositories() {
        progressBar.visibility = View.VISIBLE
        val gitHubRepositories: GitHubRepositories = RetrofitInstance.getEndPoint()
        val call: Call<ArrayList<Repository>> = gitHubRepositories.fetchAllPublicRepositories()
        call.enqueue(generateRepositoryFetch())
    }

    private fun generateRepositoryFetch(): Callback<ArrayList<Repository>> {
        return object : Callback<ArrayList<Repository>> {

            override fun onFailure(call: Call<ArrayList<Repository>>, t: Throwable) {
                mainEmptyText.text = "Something went wrong!\n${t.message}"
            }

            override fun onResponse(call: Call<ArrayList<Repository>>, response: Response<ArrayList<Repository>>) {
                if (!response.isSuccessful) {
                    mainEmptyText.text = "code: ${response.code()}"
                    return
                }
                showArticlesOnRecyclerView(response)
            }
        }
    }

    private fun showArticlesOnRecyclerView(repos: Response<ArrayList<Repository>>) {
        val temp: ArrayList<Repository>? = repos.body()
        if (temp != null && temp.size > 0) {
            progressBar.visibility = View.GONE
            loading = true
            setUpAdapter(temp)
            mainEmptyText.visibility = View.GONE
            mainretryFetchButton.visibility = View.GONE
            mainRecyclerView.visibility = View.VISIBLE
        } else {
            mainRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            mainEmptyText.visibility = View.VISIBLE
            mainretryFetchButton.visibility = View.VISIBLE
            mainretryFetchButton.setOnClickListener { fetchAllPublicRepositories() }
        }
    }

    private fun setUpAdapter(repos: ArrayList<Repository>) {
        if (repositoriesList.size == 0) {
            repositoriesList = repos
            repositoryAdapter = RepositoryAdapter(repositoriesList)
            mainRecyclerView.adapter = repositoryAdapter
        } else {
            val currentPosition = (mainRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            repositoriesList.addAll(repos)
            repositoryAdapter.notifyDataSetChanged()
            mainRecyclerView.scrollToPosition(currentPosition)
        }
        mainRecyclerView.addOnScrollListener(checkNumberOfScrolledItems())
    }

    private fun checkNumberOfScrolledItems(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItemCount =
                        (mainRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItemCount) >= 8 && totalItemCount >= PAGE_SIZE) {
                            loading = false
                            pageId++
                            Log.v("page", "${pageId}")
                            progressBar.visibility = View.GONE
                            fetchAllPublicRepositories()
                        }
                    }
                }
            }

        }
    }
}
