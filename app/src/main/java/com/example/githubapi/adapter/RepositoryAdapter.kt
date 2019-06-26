package com.example.githubapi.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.githubapi.R
import com.example.githubapi.model.Repository
import com.example.githubapi.ui.RepositoryDetailActivity
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryAdapter(
    private var repositoryList: ArrayList<Repository>
) : RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    //Store context in field for handling clicks on item.
    private lateinit var viewGroupContext: Context

    override fun getItemCount(): Int {
       return repositoryList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemIndex: Int): RepositoryViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_repository, viewGroup, false)
        return RepositoryViewHolder(itemView)
    }

    override fun onBindViewHolder(repositoryViewHolder: RepositoryViewHolder, itemIndex: Int) {
        val repository: Repository = repositoryList.get(itemIndex)
        setPropertiesRepositoryViewHolder(repositoryViewHolder, repository)
        repositoryViewHolder.cardView.setOnClickListener {
            setIntentForRepositoryDetail(repository)
        }
    }

    private fun setPropertiesRepositoryViewHolder(repositoryViewHolder: RepositoryViewHolder, repository: Repository) {
        repositoryViewHolder.name.text = repository.name
        repositoryViewHolder.fullName.text = repository.full_name
    }

    fun setRepositories(repositories: ArrayList<Repository>){
        repositoryList = repositories
        notifyDataSetChanged()
    }

    private fun setIntentForRepositoryDetail(repository: Repository){
        val repositoryDetailIntent = Intent(viewGroupContext, RepositoryDetailActivity::class.java)
        repositoryDetailIntent.putExtra(FULL_NAME, repository.full_name)
        viewGroupContext.startActivity(repositoryDetailIntent)
    }

    companion object {
        val FULL_NAME = "full_name"
    }

    inner class RepositoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val cardView: CardView by lazy { view.mainCardView }
        val name: TextView by lazy { view.mainRepoName }
        val fullName: TextView by lazy { view.repo_full_name }
    }
}