package com.patrykkosieradzki.qrcodereader.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.application.App
import com.patrykkosieradzki.qrcodereader.model.User
import com.patrykkosieradzki.qrcodereader.repository.OnCompleteListener
import com.patrykkosieradzki.qrcodereader.repository.UserRepository
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity
import com.patrykkosieradzki.qrcodereader.utils.DeviceUtils
import kotlinx.android.synthetic.main.activity_login222.*
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity(), AnkoLogger {
    enum class SignInOption {
        GOOGLE, ANONYMOUS
    }

    companion object {
        private const val GOOGLE_SIGN_IN = 0
    }

    // fake DI
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDatabase: DatabaseReference
    private lateinit var userRepository: UserRepository

    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fakeDI()
        userRepository = UserRepository(mDatabase.child("users"))

        dialog = indeterminateProgressDialog(message = "Signing in...").apply { hide() }

        signInButton.setOnClickListener { signIn(SignInOption.GOOGLE) }
        continueWithoutSigningInButton.setOnClickListener { signIn(SignInOption.ANONYMOUS) }
    }

    private fun fakeDI() {
        mGoogleSignInClient = App.instance.mGoogleSignInClient
        mAuth = App.instance.mAuth

        mDatabase = App.instance.mDatabase
    }

    private fun signIn(option: SignInOption) {
        if (DeviceUtils.isNetworkAvailable(this)) {
            when (option) {
                SignInOption.GOOGLE ->
                    startActivityForResult(mGoogleSignInClient.signInIntent, GOOGLE_SIGN_IN)

                SignInOption.ANONYMOUS ->
                    firebaseAuth(option)
            }
        } else {
            App.instance.toast("No internet connection")
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                firebaseAuth(SignInOption.GOOGLE, account)

            } catch (e: ApiException) {
                App.instance.toast("Authentication failed.")
                warn("Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuth(option: SignInOption, account: GoogleSignInAccount? = null) {
        dialog.show()

        when (option) {
            SignInOption.GOOGLE -> {
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                mAuth.signInWithCredential(credential)
            }

            SignInOption.ANONYMOUS -> {
                mAuth.signInAnonymously()
            }

        }.addOnCompleteListener { resolveTask(option, it) }
    }

    private fun resolveTask(option: SignInOption, task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val uid = task.result.user.uid
            saveUserToDatabase(User(uid))

        } else {
            App.instance.toast("Authentication failed.")
            warn("${option.name} signIn:failure", task.exception)
        }
    }

    private fun saveUserToDatabase(user: User) {
        userRepository.add(user, object : OnCompleteListener {
            override fun onComplete() {
                dialog.hide()
                finishActivity()
            }

            override fun onError() {
                // TODO: Handle error
            }
        })
    }

    override fun onResume() {
        super.onResume()

        if (mAuth.currentUser != null) {
            finishActivity()
        }
    }

    private fun finishActivity() {
        startActivity<HomeActivity>()
        finish()
    }


}
