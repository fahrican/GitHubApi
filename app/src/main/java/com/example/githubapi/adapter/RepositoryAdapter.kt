package com.example.githubapi.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.githubapi.R
import com.example.githubapi.model.Repository
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
            //setIntentForNewsDetail(repository)
            Toast.makeText(viewGroupContext, "Click listener", Toast.LENGTH_LONG).show()
        }
    }

    private fun setPropertiesRepositoryViewHolder(repositoryViewHolder: RepositoryViewHolder, repository: Repository) {
        repositoryViewHolder.name.text = repository.name
        repositoryViewHolder.fullName.text = repository.full_name
    }

    inner class RepositoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val cardView: CardView by lazy { view.card_view }
        val name: TextView by lazy { view.repo_name }
        val fullName: TextView by lazy { view.repo_full_name }
    }
}