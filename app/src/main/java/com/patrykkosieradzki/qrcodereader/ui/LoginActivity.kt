package com.patrykkosieradzki.qrcodereader.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.application.App
import com.patrykkosieradzki.qrcodereader.extensions.edit
import com.patrykkosieradzki.qrcodereader.extensions.getPreferences
import com.patrykkosieradzki.qrcodereader.model.User
import com.patrykkosieradzki.qrcodereader.repository.OnCompleteListener
import com.patrykkosieradzki.qrcodereader.repository.UserRepository
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity
import com.patrykkosieradzki.qrcodereader.utils.DateUtils
import com.patrykkosieradzki.qrcodereader.utils.DeviceUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 0
    }

    // fake DI
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDatabase: DatabaseReference
    private lateinit var userRepository: UserRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fakeDI()
        userRepository = UserRepository(mDatabase.child("users"))

        signInButton.setOnClickListener { startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN) }
        continueWithoutSigningInButton.setOnClickListener { firebaseAnonymousAuth() }
    }

    private fun fakeDI() {
        mGoogleSignInClient = App.instance.mGoogleSignInClient
        mAuth = App.instance.mAuth

        mDatabase = App.instance.mDatabase
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)

            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                handleError()
            }

        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id!!)

        // TODO: add progress dialog
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")

                        val uid = task.result.user.uid
                        val currentDate = DateUtils.currentDateAsString
                        val user = User(uid, currentDate)

                        userRepository.add(user, object : OnCompleteListener {
                            override fun onComplete() {
                                finishActivity()
                            }

                            override fun onError() {
                                // TODO: Handle error
                            }
                        })


                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        handleError()
                    }
                }
    }

    private fun firebaseAnonymousAuth() {
        if (DeviceUtils.isNetworkAvailable(this)) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInAnonymously:success uid:" + task.result.user.uid)

                            val uid = task.result.user.uid
                            val currentDate = DateUtils.currentDateAsString
                            val user = User(uid, currentDate)

                            userRepository.add(user, object : OnCompleteListener {
                                override fun onComplete() {
                                    finishActivity()
                                }

                                override fun onError() {
                                    // TODO: Handle error
                                }
                            })

                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.exception)
                            handleError()
                        }
                    }
        } else {
            handleError()
        }
    }

    private fun handleError() {
        val text: String = if (!DeviceUtils.isNetworkAvailable(applicationContext)) {
            "No network connection available."
        } else {
            "Authentication failed."
        }

        Snackbar.make(layout, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        if (mAuth.currentUser != null) {
            finishActivity()
        }
    }

    private fun finishActivity() {
        updateLoginState(1)
        startActivity<HomeActivity>()
        finish()
    }

    private fun updateLoginState(state: Int) {
        getPreferences().edit {
            putInt("logged_in", state)
        }

    }



}