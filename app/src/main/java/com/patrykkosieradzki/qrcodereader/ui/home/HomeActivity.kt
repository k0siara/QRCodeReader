package com.patrykkosieradzki.qrcodereader.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.ButterKnife
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.application.App
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.repository.OnCompleteListener
import com.patrykkosieradzki.qrcodereader.repository.QRCodeRepository
import com.patrykkosieradzki.qrcodereader.ui.LoginActivity
import com.patrykkosieradzki.qrcodereader.ui.QRActivity
import com.patrykkosieradzki.qrcodereader.ui.details.DetailsActivity
import com.patrykkosieradzki.qrcodereader.ui.home.adapter.BarcodeListAdapter
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

    private lateinit var mAdapter: BarcodeListAdapter

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initToolbarMenu()

        fakeDI()

        val query = mDatabase.child("users").child(mAuth.currentUser!!.uid).child("qrCodes")
        qrCodeRepository = QRCodeRepository(query)


        handler = Handler()

        setRecyclerView()
        showFAB()

        fab.setOnClickListener { startActivityForResult(Intent(this, QRActivity::class.java), QR_READ) }
    }

    private fun fakeDI() {
        mAuth = App.instance.mAuth
        mGoogleSignInClient = App.instance.mGoogleSignInClient
        mDatabase = App.instance.mDatabase
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
        val query = mDatabase.child("users").child(mAuth.currentUser!!.uid).child("qrCodes") // TODO: throws exception when no internet TODO: change to "barcodes"
        val options = FirebaseRecyclerOptions.Builder<QRCode>()
                .setQuery(query, QRCode::class.java)
                .build()

        mAdapter = BarcodeListAdapter(options, this)
        mAdapter.startListening()

        mAdapter.setOnClickListener(object : BarcodeListAdapter.OnClickListener {
            override fun onIconClick(model: QRCode, position: Int) {
                mAdapter.toggleSelection(position)
            }

            override fun onClick(model: QRCode, position: Int) {
                startActivity<DetailsActivity>("text" to model.text, "type" to model.type)
            }

            override fun onLongClick(model: QRCode, position: Int) {
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

            val qrCode = data?.getSerializableExtra("qrCode")
            if (qrCode != null) {
                saveQRToDatabase(qrCode as QRCode)
                //showDetails(text, type)

            } else {
                App.instance.toast("No QR Code Found")
            }
        }
    }

    private fun saveQRToDatabase(qrCode: QRCode) {
        qrCodeRepository.add(qrCode, object : OnCompleteListener {
            override fun onComplete() {

            }

            override fun onError() {

            }

        })
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
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
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
        mAdapter.startListening()
        showFAB()
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
