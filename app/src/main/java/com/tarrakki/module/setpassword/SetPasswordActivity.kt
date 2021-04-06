package com.tarrakki.module.setpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.IS_FROM_FORGOT_PASSWORD
import com.tarrakki.R
import com.tarrakki.api.ApiClient
import com.tarrakki.databinding.ActivitySetPasswordBinding
import com.tarrakki.fcm.onLoginEventFire
import com.tarrakki.fcm.onSignUpEventFire
import com.tarrakki.module.checkkycstatusbypan.CheckKYCStatusByPAN
import com.tarrakki.module.forgotpassword.FORGOTPASSWORD_DATA
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LOGIN_DATA
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.login.NewLoginActivity
import com.tarrakki.module.login.NewLoginVM
import com.tarrakki.module.otp.OtpVerificationActivity
import com.tarrakki.module.register.SIGNUP_DATA
import com.tarrakki.module.verifysocialmobilenumber.VerifyMobileNumberActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_set_password.*
import kotlinx.android.synthetic.main.activity_set_password.btnContinue
import kotlinx.android.synthetic.main.activity_set_password.ivBack
import kotlinx.android.synthetic.main.activity_set_password.tvForgotPassword
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.*

class SetPasswordActivity : CoreActivity<SetPasswordVM, ActivitySetPasswordBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_set_password
    }

    override fun createViewModel(): Class<out SetPasswordVM> {
        return SetPasswordVM::class.java
    }

    override fun setVM(binding: ActivitySetPasswordBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun createReference() {
        var data: JSONObject? = null
        ivBack.setOnClickListener {
            finish()
        }

        ivEye.setOnClickListener {
            etPassword.transformationMethod = if (getViewModel().isPasswordShow.get()!!) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            getViewModel().isPasswordShow.set(!getViewModel().isPasswordShow.get()!!)
            etPassword.setSelection(etPassword.length())
        }

        if (intent.hasExtra(LOGIN_DATA)) {
            data = JSONObject(intent.getStringExtra(LOGIN_DATA))
            etPassword.setHint(R.string.enter_password)
            getViewModel().userName.set(data.optString("username"))
            tvNotSpam.visibility = View.GONE
        }

        if (intent.hasExtra(SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SIGNUP_DATA))
            etPassword.setHint(R.string.set_password)
            tvForgotPassword.visibility = View.GONE
            getViewModel().email.set(data.optString("email"))
            getViewModel().mobile.set(data.optString("mobile"))

        }

        if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
            data = JSONObject(intent.getStringExtra(FORGOTPASSWORD_DATA))
            etPassword.setHint(R.string.create_a_new_password)
            tvForgotPassword.visibility = View.GONE
            getViewModel().token.set(data.optString("token"))
            tvNotSpam.visibility = View.GONE
        }

        btnContinue.setOnClickListener {
            it.dismissKeyboard()
            when {
                getViewModel().password.get()?.length == 0 -> {
                    simpleAlert(getString(R.string.pls_enter_password)) {
                        edtPassword?.requestFocus()
                    }
                }
                !getViewModel().password.get()?.isValidPassword()!! -> {
                    simpleAlert(getString(R.string.valid_password)) {
                        edtPassword?.requestFocus()
                    }
                }
                else -> {
                    if (intent.hasExtra(LOGIN_DATA)) {
                        getViewModel().doLogin()
                    }
                    if (intent.hasExtra(SIGNUP_DATA)) {
                        getViewModel().onSignUp()
                    }
                    if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
                        getViewModel().resetPassword().observe(this, Observer { apiResponse ->
                            simpleAlert(getString(R.string.alert_profile_reset)) {
                                val intent = Intent(this, NewLoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        })
                    }

                }
            }
        }

        tvForgotPassword.setOnClickListener {
            getViewModel().forgotPassword().observe(this, Observer { apiResponse ->
                val intent = Intent(this, VerifyMobileNumberActivity::class.java)
                intent.putExtra(FORGOTPASSWORD_DATA, getOtpData(apiResponse?.otp, apiResponse?.otpId).toString())
                intent.putExtra(IS_FROM_FORGOT_PASSWORD, true)
                startActivity(intent)
                finish()
            })
        }



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
                startActivity<HomeActivity>()
                setIsLogin(true)
                setSocialLogin(false)
                finish()
            }
        })

        getViewModel().onSignUp.observe(this, Observer { signUpResponse ->
            signUpResponse?.let {
                signUpResponse.token?.let { it1 -> setLoginToken(it1) }
                var bundle = Bundle()
                signUpResponse.userId?.let { it1 ->
                    setUserId(it1)
                    bundle.putString("user_id", it1)
                }
                signUpResponse.email?.let { it1 ->
                    setEmail(it1)
                    bundle.putString("email_id", it1)
                }

                signUpResponse.mobile?.let { it1 ->
                    setMobile(it1)
                    bundle.putString("mobile_number", it1)
                }
                onSignUpEventFire(bundle)
                signUpResponse.isMobileVerified?.let { it1 -> setMobileVerified(it1) }
                signUpResponse.isEmailActivated?.let { it1 -> setEmailVerified(it1) }
                signUpResponse.isKycVerified?.let { it1 -> setKYClVarified(it1) }
                signUpResponse.completeRegistration?.let { it1 -> setCompletedRegistration(it1) }
                signUpResponse.kycStatus?.let { App.INSTANCE.setKYCStatus(it) }
                signUpResponse.isRemainingFields?.let { App.INSTANCE.setRemainingFields(it) }

                setIsLogin(true)
                setSocialLogin(false)
                startActivity<HomeActivity>()
                finishAffinity()
            }
        })


    }

    private fun getOtpData(otp: String?, otp_id: String?): JsonObject {
        val json = JsonObject()
        json.addProperty("otp", otp)
        json.addProperty("otp_id", otp_id)
        json.addProperty("email", "${getViewModel().userName.get()}".toLowerCase().trim())
        return json
    }
}