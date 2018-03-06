package com.patrykkosieradzki.qrcodereader.ui.details

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.patrykkosieradzki.qrcodereader.R

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            if (intent.hasExtra("text") && intent.hasExtra("type")) {

                val text = intent.getStringExtra("text")
                val type = intent.getStringExtra("type")
                supportActionBar?.title = type

                supportFragmentManager.beginTransaction().run {
                    replace(R.id.fragment, TextFragment.newInstance(text))
                    commit()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu) // TODO: change later
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
            android.R.id.home -> {
                // TODO: Handle navigation flow
                NavUtils.navigateUpFromSameTask(this)
                true
            }

            else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)

        super.onBackPressed()
    }
}
