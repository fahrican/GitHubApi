package com.example.githubapi.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_contributor.view.*

class ContributorAdapter {

    inner class ContributorViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val cardView: CardView by lazy { view.contributorCardView }
        val avatarUrl: ImageView by lazy { view.contributorAvatar }
        val login: TextView by lazy { view.loginTextView }
    }
}