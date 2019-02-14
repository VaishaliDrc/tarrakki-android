package com.tarrakki.module.socialauthhelper

import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import org.supportcompact.ktx.e

class GoogleSignInHelper(val activity : AppCompatActivity,
                         val mGoogleSignInListener : GoogleSignInListener) {

    val TAG = "GoogleSIGNIN"
    private var RC_GOOGLE_SIGN_IN = 260
    private var mGoogleSignInClient : GoogleSignInClient ?= null

    init {
        setUpGoogleSignIn()
    }

    fun setUpGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            mGoogleSignInListener.onGoogleSignInSuccess(account)
            mGoogleSignInClient?.signOut()
        } catch (e: ApiException) {
            activity.e(TAG, "signInResult:failed code=" + e.statusCode)
            mGoogleSignInListener.onGoogleSignInFailed(e)
            mGoogleSignInClient?.signOut()
        }
    }
}

interface GoogleSignInListener{
    fun onGoogleSignInSuccess(googleSignInAccount: GoogleSignInAccount)

    fun onGoogleSignInFailed(e: ApiException)
}