package com.patrykkosieradzki.qrcodereader.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.patrykkosieradzki.qrcodereader.R;
import com.patrykkosieradzki.qrcodereader.application.App;
import com.patrykkosieradzki.qrcodereader.model.User;
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity;
import com.patrykkosieradzki.qrcodereader.utils.DateUtils;
import com.patrykkosieradzki.qrcodereader.utils.DeviceUtils;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }


    @OnClick(R.id.signInButton)
    public void onSignInButtonClick() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.continueWithoutSigningInButton)
    public void onContinueWithoutSigningInButtonClick() {
        firebaseAnonymousAuth();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                handleError();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        // TODO: add progress dialog
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        User user = new User(
                                task.getResult().getUser().getUid(),
                                DateUtils.getCurrentDateAsString()
                        );

                        submitUser(user);
                        finishActivity();

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        handleError();
                    }
                });
    }

    private void firebaseAnonymousAuth() {
        if (DeviceUtils.INSTANCE.isNetworkAvailable(this)) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success uid:" + task.getResult().getUser().getUid());


                            User user = new User(
                                    task.getResult().getUser().getUid(),
                                    DateUtils.getCurrentDateAsString()
                            );

                            submitUser(user);
                            finishActivity();

                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            handleError();
                        }
                    });
        } else {
            handleError();
        }
    }

    private void submitUser(User user) {
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User databaseUser = dataSnapshot.getValue(User.class);

                if (databaseUser == null) {
                    writeNewUser(user);
                    Log.d(TAG, "onDataChange: New user " + user.getUid() + " added to database");
                } else {
                    Log.d(TAG, "onDataChange: User already in the database, skipping adding new user to the database");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCanceled: Failed to read user from the database");

            }
        });
    }

    private void writeNewUser(User user) {
        mDatabase.child("users").child(user.getUid()).setValue(user);
    }

    private void handleError() {
        String text;
        if (!DeviceUtils.INSTANCE.isNetworkAvailable(getApplicationContext())) {
            text = "No network connection available.";
        } else {
            text = "Authentication failed.";
        }

        Snackbar.make(findViewById(R.id.layout), text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAuth.getCurrentUser() != null) {
            finishActivity();
        }
    }

    private void finishActivity() {
        updateLoginState(1);
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void updateLoginState(int state) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("logged_in", state);
        editor.apply();
    }

}
