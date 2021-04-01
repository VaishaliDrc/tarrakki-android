package com.tarrakki.module.register

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.IS_FROM_INTRO
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.databinding.ActivityRegisterBinding
import com.tarrakki.fcm.onLoginEventFire
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.otp.OtpVerificationActivity
import com.tarrakki.module.socialauthhelper.GoogleSignInHelper
import com.tarrakki.module.socialauthhelper.GoogleSignInListener
import com.tarrakki.module.webviewActivity.CMSPagesActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.edtPassword
import kotlinx.android.synthetic.main.activity_register.llGpl
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreActivity
import org.supportcompact.events.Event
import org.supportcompact.ktx.*

const val SIGNUP_DATA = "signup_data"
const val IS_EMAIL_VALIDATOR = "is_email_validator"
const val SIGNUP_OTP_DATA = "signup_otp_data"

class RegisterActivity : CoreActivity<RegisterVM, ActivityRegisterBinding>(), GoogleSignInListener {

    var mGoogleSignInHelper: GoogleSignInHelper? = null
    var callbackManager: CallbackManager? = null

    override fun getLayout(): Int {
        return R.layout.activity_register
    }

    override fun createViewModel(): Class<out RegisterVM> {
        return RegisterVM::class.java
    }

    override fun setVM(binding: ActivityRegisterBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        mGoogleSignInHelper = GoogleSignInHelper(this, this)

        tvAlreadyHasAccount?.setOnClickListener {
            if (intent.hasExtra(IS_FROM_INTRO)) {
                startActivity<LoginActivity>()
                finish()
            } else {
                onBackPressed()
            }
        }

        val termsAndCondditionClickSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                startActivity<CMSPagesActivity>()
                postSticky(Event.TERMS_AND_CONDITIONS_PAGE)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                color(R.color.colorAccent).let { ds.color = it }
            }
        }


        llGpl?.setOnClickListener {
            EventBus.getDefault().post(SHOW_PROGRESS)
            mGoogleSignInHelper?.signIn()
        }

        getViewModel().onSocialLogin.observe(this, Observer {
            it?.let { apiResponse ->
                val json = JsonObject()
                if (apiResponse.status?.code == 3) {
                    json.addProperty("access_token", getViewModel().socialId.get())
                    json.addProperty("email", "${getViewModel().socialEmail.get()}".toLowerCase().trim())
                    json.addProperty("first_name", getViewModel().socialFName.get())
                    json.addProperty("last_name", getViewModel().socialLName.get())
                    json.addProperty("social_auth", apiResponse.status.message)
                    val intent = Intent(this, SocialSignUpActivity::class.java)
                    intent.putExtra(SOACIAL_SIGNUP_DATA, json.toString())
                    startActivity(intent)
                } else if (apiResponse.status?.code == 2) {
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra(SOACIAL_SIGNUP_DATA, json.toString())
                    startActivity(intent)
                    EventBus.getDefault().postSticky(apiResponse)
                }
            }
        })

        getViewModel().onLogin.observe(this, Observer { loginResponse ->
            loginResponse?.let {
                ApiClient.clear()
                loginResponse.token?.let { it1 -> setLoginToken(it1) }
                var bundle = Bundle()
                loginResponse.userId?.let { it1 ->
                    setUserId(it1)
//                    onLoginEventFire(it1)
                    bundle.putString("user_id", it1)
                }
                loginResponse.email?.let { it1 -> setEmail(it1)
                    bundle.putString("email_id", it1)
                }
                loginResponse.mobile?.let { it1 -> setMobile(it1)
                    bundle.putString("mobile_number", it1)
                }
                onLoginEventFire(bundle)
                loginResponse.isMobileVerified?.let { it1 -> setMobileVerified(it1) }
                loginResponse.isEmailActivated?.let { it1 -> setEmailVerified(it1) }
                loginResponse.isKycVerified?.let { it1 -> setKYClVarified(it1) }
                loginResponse.completeRegistration?.let { it1 -> setCompletedRegistration(it1) }
                loginResponse.readyToInvest?.let { it1 -> setReadyToInvest(it1) }
                loginResponse.kycStatus?.let { App.INSTANCE.setKYCStatus(it) }
                loginResponse.isRemainingFields?.let { App.INSTANCE.setRemainingFields(it) }
                startActivity<HomeActivity>()
                setIsLogin(cbKeepMeSignIn.isChecked)
                finish()
            }
        })


        cbTermsConditions?.makeLinks(arrayOf("Terms and Conditions"), arrayOf(termsAndCondditionClickSpan))

        btnSignUp?.setOnClickListener {

            if (getViewModel().email.get()?.length == 0) {
                simpleAlert(getString(R.string.pls_enter_email_address)) {
                    edtEmail.requestFocus()
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(getViewModel().email.get()).matches()) {
                simpleAlert(getString(R.string.pls_enter_valid_email_address)) {
                    edtEmail?.requestFocus()
                }
            } else if (getViewModel().mobile.get()?.length == 0) {
                simpleAlert(getString(R.string.pls_enter_mobile_number)) {
                    edtMobile?.requestFocus()
                }
            } else if (!getViewModel().mobile.isValidMobile()) {
                simpleAlert(getString(R.string.pls_enter_valid_indian_mobile_number)) {
                    edtMobile?.requestFocus()
                }
            } else if (getViewModel().password.get()?.length == 0) {
                simpleAlert(getString(R.string.pls_enter_password)) {
                    edtPassword?.requestFocus()
                }
            } else if (!getViewModel().password.isValidPassword()) {
                simpleAlert(getString(R.string.valid_password)) {
                    edtPassword?.requestFocus()
                    edtPassword?.setSelection(edtPassword.text.length)
                }
            } else if (getViewModel().confirmPassword.get()?.length == 0) {
                simpleAlert(getString(R.string.pls_enter_confirm_password)) {
                    edtPassword?.requestFocus()
                }
            } else if (getViewModel().confirmPassword.get() != getViewModel().password.get()) {
                simpleAlert(getString(R.string.alert_mismatch_reg_password)) {
                    edtConfirmPassword?.requestFocus()
                }
            } else if (cbTermsConditions?.isChecked == false) {
                simpleAlert(getString(R.string.alert_req_terms)) {
                    edtConfirmPassword?.requestFocus()
                }
            } else {
                it.dismissKeyboard()
                getViewModel().getOTP(getViewModel().mobile.get(), getViewModel().email.get()).observe(this, Observer {
                    it?.let { it1 ->
                        val intent = Intent(this, OtpVerificationActivity::class.java)
                        intent.putExtra(SIGNUP_DATA, getViewModel().getSignUpData().toString())
                        intent.putExtra(IS_EMAIL_VALIDATOR, false)
                        startActivity(intent)
                        EventBus.getDefault().postSticky(it1)
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mGoogleSignInHelper?.onActivityResult(requestCode, resultCode, data)
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
            getViewModel().socialEmail.set(personEmail)
            getViewModel().socialFName.set(fname)
            getViewModel().socialLName.set(lname)
            getViewModel().socialId.set(personId)
            if (getViewModel().socialEmail.isEmpty()) {
                simpleAlert(App.INSTANCE.getString(R.string.we_could_not_retrieve))
                /*val json = JsonObject()
                json.addProperty("access_token", getViewModel().socialId.get())
                json.addProperty("email", "")
                json.addProperty("first_name", getViewModel().socialFName.get())
                json.addProperty("last_name", getViewModel().socialLName.get())
                json.addProperty("social_auth", "google")
                val intent = Intent(this@LoginActivity, SocialSignUpActivity::class.java)
                intent.putExtra(SOACIAL_SIGNUP_DATA, json.toString())
                startActivity(intent)*/
            } else {
                getViewModel().doSocialLogin(loginWith = "google")
            }
        }
    }
}
