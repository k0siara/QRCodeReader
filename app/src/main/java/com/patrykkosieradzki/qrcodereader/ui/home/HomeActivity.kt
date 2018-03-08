package com.patrykkosieradzki.qrcodereader.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.patrykkosieradzki.qrcodereader.ui.home.adapter.BarcodeListAdapter
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.ui.details.DetailsActivity
import com.patrykkosieradzki.qrcodereader.utils.DateUtils

import butterknife.ButterKnife
import butterknife.OnClick
import com.patrykkosieradzki.qrcodereader.application.App
import com.patrykkosieradzki.qrcodereader.extensions.edit
import com.patrykkosieradzki.qrcodereader.extensions.getPreferences
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.model.User
import com.patrykkosieradzki.qrcodereader.repository.OnCompleteListener
import com.patrykkosieradzki.qrcodereader.repository.QRCodeRepository
import com.patrykkosieradzki.qrcodereader.repository.UserRepository
import com.patrykkosieradzki.qrcodereader.ui.LoginActivity
import com.patrykkosieradzki.qrcodereader.ui.QRActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "HomeActivity"
        const val QR_READ = 0
    }

    // fake DI
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mDatabase: DatabaseReference

    private lateinit var qrCodeRepository: QRCodeRepository

    private lateinit var mCurrentUserUID: String

    private lateinit var mAdapter: BarcodeListAdapter

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        initToolbarMenu()

        // fake DI
        mAuth = App.instance.mAuth
        mGoogleSignInClient = App.instance.mGoogleSignInClient
        mDatabase = App.instance.mDatabase

        mCurrentUserUID = mAuth.currentUser?.uid!!

        val query = mDatabase.child("users").child(mCurrentUserUID).child("qrCodes")
        qrCodeRepository = QRCodeRepository(query)


        handler = Handler()

        setRecyclerView()
        showFAB()

        fab.setOnClickListener { startActivityForResult(Intent(this, QRActivity::class.java), QR_READ) }
    }

    private fun setRecyclerView() {
        setLayoutManager()
        setAdapter()

        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fab.visibility == View.VISIBLE) {
                        fab.hide(true)
                    } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                        fab.show(true)
                    }
                }
            })
        }
    }

    private fun setLayoutManager() {
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
    }

    private fun setAdapter() {
        val query = mDatabase.child("users").child(mCurrentUserUID).child("qrCodes") // TODO: throws exception when no internet TODO: change to "barcodes"
        val options = FirebaseRecyclerOptions.Builder<QRCode>()
                .setQuery(query, QRCode::class.java)
                .build()

        mAdapter = BarcodeListAdapter(options, this)
        mAdapter.startListening()

        mAdapter.setOnClickListener(object : BarcodeListAdapter.OnClickListener {
            override fun onIconClick(model: QRCode, position: Int) {
                mAdapter.toggleSelection(position)
            }

            override fun onContentClick(model: QRCode, position: Int) {
                startActivity<DetailsActivity>("text" to model.text, "type" to model.type)
            }

            override fun onContentLongClick(model: QRCode, position: Int) {
                mAdapter.toggleSelection(position)
            }
        })

        recyclerView.adapter = mAdapter
    }

    private fun showFAB() {
        fab.hide(false)
        handler.postDelayed({ fab.show(true) }, 400)
    }

    private fun hideFAB() {
        fab.hide(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == QR_READ) {
            data?.extras?.run {
                val text = this.get("text").toString()
                val type = this.get("type").toString()

                val qrCode = QRCode(text, type, DateUtils.currentDateAsString)
                qrCodeRepository.add(qrCode, object : OnCompleteListener {
                    override fun onComplete() {

                    }

                    override fun onError() {

                    }

                })

                //showDetails(text, type)
            }

            val text = if (data != null) data.extras!!.get("text")!!.toString() else "No QR Code Found."
            Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun initToolbarMenu() {
        toolbar.title = getString(R.string.app_name)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_how_it_works -> true
                R.id.action_settings -> true
                R.id.action_logout -> logout()
                else -> App.instance.toast("Unknown option")
            }
            true
        }
    }

    private fun logout() {
        mAdapter.stopListening()
        recyclerView.adapter = null

        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            updateLoginState()
            startActivity<LoginActivity>()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        hideFAB()
    }

    override fun onResume() {
        super.onResume()
        showFAB()

        mAdapter.startListening()
    }

    private fun updateLoginState() {
        getPreferences().edit {
            putInt("logged_in", 0)
        }
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.stopListening()
        recyclerView.adapter = null
    }


}
