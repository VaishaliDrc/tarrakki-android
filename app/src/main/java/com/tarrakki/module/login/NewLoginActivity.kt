package com.tarrakki.module.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.api.model.*
import com.tarrakki.databinding.ActivityNewLoginBinding
import com.tarrakki.databinding.LayoutLoginIntroducationItemBinding
import com.tarrakki.fcm.onLoginEventFire
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.register.SIGNUP_DATA
import com.tarrakki.module.register.SOACIAL_SIGNUP_DATA
import com.tarrakki.module.setpassword.SetPasswordActivity
import com.tarrakki.module.socialauthhelper.GoogleSignInHelper
import com.tarrakki.module.socialauthhelper.GoogleSignInListener
import com.tarrakki.module.verifymobileoremail.UserMobileEmailInputActivity
import com.tarrakki.module.verifymobileoremail.VerifyMobileOrEmailActivity
import com.tarrakki.module.verifymobileoremail.VerifyMobileOrEmailVM
import com.tarrakki.module.verifysocialmobilenumber.EnterMobileNumberActivity
import com.tarrakki.module.verifysocialmobilenumber.VerifyMobileNumberActivity
import com.tarrakki.module.webviewActivity.CMSPagesActivity
import kotlinx.android.synthetic.main.activity_new_login.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.adapters.setPageAdapter

const val LOGIN_DATA = "login_data"
class NewLoginActivity : CoreActivity<NewLoginVM, ActivityNewLoginBinding>(), GoogleSignInListener {

    var mGoogleSignInHelper: GoogleSignInHelper? = null

    override fun getLayout(): Int {
        return R.layout.activity_new_login
    }

    override fun createViewModel(): Class<out NewLoginVM> {
        return NewLoginVM::class.java
    }

    override fun setVM(binding: ActivityNewLoginBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun createReference() {
        mGoogleSignInHelper = GoogleSignInHelper(this, this)
        setAdapter()

        tvTermsConditions.makeSpannableLinks(resources.getColor(R.color.auto_cancel),
                Pair("Terms & conditions", View.OnClickListener {
                    startActivity<CMSPagesActivity>()
                    postSticky(Event.TERMS_AND_CONDITIONS_PAGE)
                }),
                Pair("privacy policy", View.OnClickListener {
                    startActivity<CMSPagesActivity>()
                    postSticky(Event.PRIVACY_PAGE)
                }))
        btnContinue.setOnClickListener {
            if (etEmail.text.isNotEmpty() && (etEmail.text.toString().isValidMobile() || etEmail.text.toString().isEmail())) {
                getViewModel().doNormalLogin(etEmail.text.toString())
            } else {
                simpleAlert(getString(R.string.pls_enter_valid_mobile_number_or_email))
            }
        }

        ivGoogle?.setOnClickListener {
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
                    val intent = Intent(this, EnterMobileNumberActivity::class.java)
                    intent.putExtra(SOACIAL_SIGNUP_DATA, json.toString())
                    startActivity(intent)
                } else if (apiResponse.status?.code == 2) {
                    val intent = Intent(this, VerifyMobileNumberActivity::class.java)
                    val jsonObj = JSONObject(apiResponse.data?.toDecrypt())
                    intent.putExtra(SOACIAL_SIGNUP_DATA, jsonObj.toString())
                    startActivity(intent)
                    EventBus.getDefault().postSticky(apiResponse)
                }
            }
        })

        getViewModel().onNormalLogin.observe(this, Observer {
            it?.let { apiResponse ->
                val json = JsonObject()
                val response = apiResponse.data?.parseTo<NormalLoginResponse>()!!.loginData
                if (apiResponse.status?.code == 2) {
                    json.addProperty("username", etEmail.text.trim().toString())
                    val intent = Intent(this, SetPasswordActivity::class.java)
                    intent.putExtra(LOGIN_DATA, json.toString())
                    startActivity(intent)

                } else if (apiResponse.status?.code == 1) {

                    if (response!!.isEmailVerified && response!!.isMobileVerified && !response.isRegistered) {
                        json.addProperty("email", response.email)
                        json.addProperty("mobile", response.mobile)
                        json.addProperty("first_name", response.firstName)
                        json.addProperty("last_name", response.last_name)

                        val intent = Intent(this, SetPasswordActivity::class.java)
                        intent.putExtra(SIGNUP_DATA, json.toString())
                        startActivity(intent)

                    } else if (response!!.isEmailVerified || response!!.isMobileVerified) {
                        json.addProperty("email", response.email)
                        json.addProperty("mobile", response.mobile)
                        json.addProperty("is_email_verified", response.isEmailVerified)
                        json.addProperty("is_mobile_verified", response.isMobileVerified)
                        val intent = Intent(this, UserMobileEmailInputActivity::class.java)
                        intent.putExtra(SIGNUP_DATA, json.toString())
                        startActivity(intent)
                    } else {
                        json.addProperty("is_mobile", response!!.isMobile)
                        json.addProperty("is_email", response!!.isEmail)
                        json.addProperty("email", response.email)
                        json.addProperty("mobile", response.mobile)
                        json.addProperty("is_email_verified", response.isEmailVerified)
                        json.addProperty("is_mobile_verified", response.isMobileVerified)
                        json.addProperty("otp_id", response.otpId)
                        json.addProperty("first_name", "")
                        json.addProperty("last_name", "")
                        val intent = Intent(this, VerifyMobileOrEmailActivity::class.java)
                        intent.putExtra(SIGNUP_DATA, json.toString())
                        startActivity(intent)
                    }
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
                loginResponse.email?.let { it1 ->
                    setEmail(it1)
                    bundle.putString("email_id", it1)
                }
                loginResponse.mobile?.let { it1 ->
                    setMobile(it1)
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
                setSocialLogin(true)
                setIsLogin(true)
                startActivity<HomeActivity>()
                finishAffinity()
            }
        })

    }


    private fun setAdapter() {
        pager_intro?.setPageAdapter(R.layout.layout_login_introducation_item, getViewModel().getLoginIntroductionList()) { binder: LayoutLoginIntroducationItemBinding, item: NewLoginVM.LoginIntroduction ->
            binder.vm = item
            binder.executePendingBindings()
        }
        pageIndicator?.setViewPager(pager_intro)
        pager_intro?.interval = 4000
        pager_intro?.startAutoScroll()
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
            } else {
                getViewModel().doSocialLogin(loginWith = "google")
            }
        }
    }


}