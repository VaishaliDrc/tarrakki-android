package com.tarrakki.module.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.tarrakki.IS_FROM_ACCOUNT
import com.tarrakki.R
import com.tarrakki.databinding.ActivityLoginBinding
import com.tarrakki.module.forgotpassword.ForgotPasswordActivity
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.register.RegisterActivity
import com.tarrakki.module.socialauthhelper.GoogleSignInHelper
import com.tarrakki.module.socialauthhelper.GoogleSignInListener
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.*
import java.util.*


class LoginActivity : CoreActivity<LoginVM, ActivityLoginBinding>(), GoogleSignInListener {

    var mGoogleSignInHelper: GoogleSignInHelper? = null
    var callbackManager: CallbackManager? = null

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun createViewModel(): Class<out LoginVM> {
        return LoginVM::class.java
    }

    override fun setVM(binding: ActivityLoginBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        mGoogleSignInHelper = GoogleSignInHelper(this, this)

        tvForgotPassword?.setOnClickListener {
            startActivity<ForgotPasswordActivity>()
        }

        tvGetStarted?.setOnClickListener {
            if (intent.hasExtra(IS_FROM_ACCOUNT)) {
                startActivity(Intent(this, RegisterActivity::class.java).apply {
                    putExtra(IS_FROM_ACCOUNT, true)
                })
            } else {
                startActivity<RegisterActivity>()
            }
        }

        btnLogin?.setOnClickListener {
            when {
                getViewModel().userName.get()?.length == 0 -> {
                    simpleAlert("Please enter email address") {
                        edtEmail?.requestFocus()
                    }
                }
                !Patterns.EMAIL_ADDRESS.matcher(getViewModel().userName.get()).matches() -> {
                    simpleAlert("Please enter valid email address") {
                        edtEmail?.requestFocus()
                    }
                }
                getViewModel().password.get()?.length == 0 -> {
                    simpleAlert("Please enter password") {
                        edtPassword?.requestFocus()
                    }
                }
                !getViewModel().password.isValidPassword() -> {
                    simpleAlert(getString(R.string.valid_password)) {
                        edtPassword?.requestFocus()
                        edtPassword?.setSelection(edtPassword.text.length)
                    }
                }
                else -> {
                    getViewModel().doLogin().observe(this, Observer { loginResponse ->
                        loginResponse?.let {
                            loginResponse.token?.let { it1 -> setLoginToken(it1) }
                            loginResponse.userId?.let { it1 -> setUserId(it1) }
                            loginResponse.email?.let { it1 -> setEmail(it1) }
                            loginResponse.mobile?.let { it1 -> setMobile(it1) }
                            loginResponse.isMobileVerified?.let { it1 -> setMobileVerified(it1) }
                            loginResponse.isEmailActivated?.let { it1 -> setEmailVerified(it1) }
                            loginResponse.isKycVerified?.let { it1 -> setKYClVarified(it1) }
                            loginResponse.completeRegistration?.let { it1 -> setCompletedRegistration(it1) }
                            startActivity<HomeActivity>()
                            setIsLogin(cbKeepMeSignIn.isChecked)
                            /*if (!intent.hasExtra(IS_FROM_ACCOUNT)) {
                                startActivity<HomeActivity>()
                            }*/

                            //App.INSTANCE.isLoggedIn.value = true
                            finish()
                        }
                    })
                    /*if (!intent.hasExtra(IS_FROM_ACCOUNT)) {
                        startActivity<HomeActivity>()
                    }
                    setIsLogin(cbKeepMeSignIn.isChecked)
                    App.INSTANCE.isLoggedIn.value = true
                    finish()*/
                }
            }
        }

        /*App.INSTANCE.isLoggedIn.observe(this, Observer {
            it?.let { isLogin ->
                if (intent.hasExtra(IS_FROM_ACCOUNT) && isLogin)
                    finish()
            }
        })*/

        llGpl?.setOnClickListener {
            EventBus.getDefault().post(SHOW_PROGRESS)
            mGoogleSignInHelper?.signIn()
        }

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // App code
                        getViewModel().showProgress()
                        val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject, response ->
                            try {
                                val socialEmail = jsonObject.optString("email")
                                val socialFirstName = jsonObject.optString("first_name")
                                val socialLastName = jsonObject.optString("last_name")
                                e("Email=>$socialEmail")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            getViewModel().dismissProgress()
                            LoginManager.getInstance().logOut()
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,email,first_name,last_name")
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }
                })

        llFB?.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mGoogleSignInHelper?.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGoogleSignInSuccess(googleSignInAccount: GoogleSignInAccount?) {
        getViewModel().dismissProgress()
        getGoogleAccountData()
    }

    override fun onGoogleSignInFailed(e: ApiException) {
        getViewModel().dismissProgress()
        e(e.localizedMessage.toString())
    }

    private fun getGoogleAccountData() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val fname = acct.givenName
            val lname = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto = acct.photoUrl
            e(personName.toString())
        }
    }
}
