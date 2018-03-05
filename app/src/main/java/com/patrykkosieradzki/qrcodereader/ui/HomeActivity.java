package com.patrykkosieradzki.qrcodereader.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.result.ParsedResultType;
import com.patrykkosieradzki.qrcodereader.FirebaseBarcodeRecyclerAdapter;
import com.patrykkosieradzki.qrcodereader.model.QRCode;
import com.patrykkosieradzki.qrcodereader.R;
import com.patrykkosieradzki.qrcodereader.model.User;
import com.patrykkosieradzki.qrcodereader.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.recyclerView) protected RecyclerView mRecyclerView;

    private static final String TAG = "HomeActivity";
    public static final int QR_READ = 0;
    private Handler handler;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        handler = new Handler();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setRecyclerView();
        showFAB();
    }

    public void setRecyclerView() {
        setLayoutManager();
        setAdapter();

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide(true);
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show(true);
                }
            }
        });

    }

    private void setLayoutManager() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAdapter() {
        Query query = mDatabase.child("users").child(mCurrentUser.getUid()).child("qrCodes"); // TODO: throws exception when no internet
        query.keepSynced(true);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<QRCode>()
                .setQuery(query, QRCode.class)
                .build();

        FirebaseBarcodeRecyclerAdapter mAdapter = new FirebaseBarcodeRecyclerAdapter(options);
        mAdapter.startListening();

        mRecyclerView.setAdapter(mAdapter);
    }

    private void showFAB() {
        fab.hide(false);
        handler.postDelayed(() -> fab.show(true), 400);
    }

    private void hideFAB() {
        fab.hide(true);
    }

    @OnClick(R.id.fab)
    public void onFABClick() {
        startActivityForResult(new Intent(HomeActivity.this, QRActivity.class), QR_READ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == QR_READ) {
            if (data != null) {

                String text = data.getStringExtra("text");
                String type = data.getStringExtra("type");
                submitQRCode(text, type);
                showDetails(text, type);
            }

            String text = data != null ? data.getExtras().get("text").toString() : "No QR Code Found.";
            Snackbar.make(findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
                    .setAction("URL", v -> {
                        Uri webpage = Uri.parse(text);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    })
                    .setAction("TEL", v -> {

                    })
                    .show();
        }
    }

    private void submitQRCode(String text, String type) {
        mDatabase.child("users").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User databaseUser = dataSnapshot.getValue(User.class);

                if (databaseUser != null) {
                    writeNewQRCode(text, type);
                    Log.d(TAG, "onDataChange: New QRCode " + text + " added to user " + mCurrentUser.getUid());

                } else {
                    Log.d(TAG, "onDataChange: User not found in the database. Trying to insert data with a non-existent account");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCanceled: Failed to read user from the database");
            }
        });
    }

    private void writeNewQRCode(String text, String type) {
        String key = mDatabase.child("users").child(mCurrentUser.getUid()).child("qrCodes").push().getKey();

        QRCode qrCode = new QRCode(text, type, DateUtils.getCurrentDateAsString());
        mDatabase.child("users").child(mCurrentUser.getUid()).child("qrCodes").child(key).setValue(qrCode);
    }

    private void showDetails(String text, String type) {
        switch (text) {
            case "TEXT":
                break;

            case "URL":
                break;

            default:
                // unsupported qr type
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_how_it_works:
                // TODO: Handle walkthrough
                return true;

            case R.id.action_settings:
                // TODO: Handle settings
                return true;

            case R.id.action_logout:
                // TODO: Handle logout
                logout();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        if (mCurrentUser != null) {
            mAuth.signOut();

            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        updateLoginState();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    });
        } else {
            // TODO: Handle user without google login

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideFAB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFAB();
    }

    private void updateLoginState() {
        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("logged_in", 0);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
    }
}
