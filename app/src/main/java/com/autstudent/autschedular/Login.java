package com.autstudent.autschedular;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by wilzo on 11/01/2016.
 */
public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;

    private boolean mustSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mustSignOut = false;

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        if (message != null) {
            Log.i("logout", message);
            switch (message) {
                case "logout":
                    mustSignOut = true;
                    break;
            }
        }
    }


    public void onStart() {
        super.onStart();
        if (isNetworkAvailable()) {
            if (!mustSignOut) {
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                if (opr.isDone()) {
                    // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                    // and the GoogleSignInResult will be available instantly.

                    Log.d(TAG, "Got cached sign-in");
                    GoogleSignInResult result = opr.get();
                    handleSignInResult(result);

                } else

                {
                    // If the user has not previously signed in on this device or the sign-in has expired,
                    // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                    // single sign-on will occur in this branch.
                    showProgressDialog();
                    opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(GoogleSignInResult googleSignInResult) {
                            hideProgressDialog();
                            handleSignInResult(googleSignInResult);
                        }
                    });
                }
            } else {
                mustSignOut = false;
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Network Issue")
                    .setMessage("Please connect to Intenet")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            try {
                GoogleSignInAccount acct = result.getSignInAccount();
                final String userEmail = acct.getEmail();
                final String firstName = acct.getDisplayName();

                final String[] cut = userEmail.split("@");
                final String userName = cut[0];
                ParseUser current = ParseUser.getCurrentUser();
                if (current != null) {
                    current.logOut();
                }
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username", userName);
                List<ParseUser> users = query.find();
                if (users.isEmpty()) {
                    Log.d("user", "not exists yet ");
                    ParseUser user = new ParseUser();
                    user.setUsername(userName);
                    user.setPassword(firstName);
                    user.put("display_name", firstName);
                    user.setEmail(userEmail);
                    user.signUp();
                } else {
                    Log.d("user", " exists already ");
                }
                ParseUser.logIn(userName, firstName);
                ParseUser.getCurrentUser().put("profile_pic",acct.getPhotoUrl().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!mustSignOut) {
                Intent newIntent = new Intent(Login.this, MainActivity.class);
                startActivity(newIntent);
            }
        } else {
            // Signed out, show unauthenticated UI.
            //do nothing
        }
    }

    private void signIn() {
        showProgressDialog();
        signOut();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mProgressDialog.dismiss();
    }

    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if (isNetworkAvailable())
                    signIn();
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Network Issue")
                            .setMessage("Please connect to Intenet")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
