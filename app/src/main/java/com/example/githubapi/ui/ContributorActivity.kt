package com.example.githubapi.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.githubapi.R

class ContributorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contributor)

        supportActionBar?.title = "List of all Contributors"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
