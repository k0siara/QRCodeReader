package com.patrykkosieradzki.qrcodereader.ui.details

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.application.App
import kotlinx.android.synthetic.main.activity_details.*
import org.jetbrains.anko.toast

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (savedInstanceState == null) {
            intent.extras?.let {
                supportFragmentManager.beginTransaction().run {
                    replace(R.id.fragment, TextFragment.newInstance(it.getString("text")))
                    commit()
                }

                toolbar.title = it.getString("type")
            }
        }

        initToolbarMenu()
        enableHomeAsUp()
    }

    private fun initToolbarMenu() {
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_how_it_works -> true
                R.id.action_settings -> true
                R.id.action_logout -> true
                else -> App.instance.toast("Unknown option")
            }
            true
        }
    }

    private fun enableHomeAsUp() {
        toolbar.navigationIcon = DrawerArrowDrawable(toolbar.context).apply { progress = 1f }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)

        super.onBackPressed()
    }
}
