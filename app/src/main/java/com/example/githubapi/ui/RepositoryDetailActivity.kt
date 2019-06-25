package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.githubapi.R
import com.example.githubapi.adapter.RepositoryAdapter
import kotlinx.android.synthetic.main.activity_repository_detail.*

class RepositoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_detail)

    }

    override fun onStart() {
        super.onStart()
        full_name.text = intent.getStringExtra(RepositoryAdapter.FULL_NAME)
    }
}
