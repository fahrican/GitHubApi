package com.example.githubapi.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.githubapi.R
import com.example.githubapi.model.Contributor
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_contributor.view.*

class ContributorAdapter(private var contributorsList: ArrayList<Contributor>) : RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder>() {

    private lateinit var viewGroupContext: Context


    override fun getItemCount(): Int {
        return contributorsList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemIndex: Int): ContributorViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_contributor, viewGroup, false)
        return ContributorViewHolder(itemView)
    }

    override fun onBindViewHolder(contributorViewHolder: ContributorViewHolder, itemIndex: Int) {
        val contributor: Contributor = contributorsList.get(itemIndex)
        setPropertiesForViewHolder(contributor, contributorViewHolder)
    }

    private fun setPropertiesForViewHolder(contributor: Contributor, contributorViewHolder: ContributorViewHolder){
        Picasso.get()
            .load(contributor.avatar_url)
            .centerCrop()
            .fit()
            .into(contributorViewHolder.avatarUrl)
    }

    fun setContributors(contributors: ArrayList<Contributor>){
        contributorsList = contributors
        notifyDataSetChanged()
    }

    inner class ContributorViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val cardView: CardView by lazy { view.contributorCardView }
        val avatarUrl: ImageView by lazy { view.contributorAvatar }
        val login: TextView by lazy { view.loginTextView }
    }
}