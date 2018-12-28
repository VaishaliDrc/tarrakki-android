package com.tarrakki.module.otp

import android.arch.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.IS_FROM_ACCOUNT
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.toDecrypt
import com.tarrakki.databinding.ActivityOtpVerificationBinding
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.register.SIGNUP_DATA
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.setIsLogin
import org.supportcompact.ktx.setLoginToken
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startActivity

class OtpVerificationActivity : CoreActivity<OptVerificationsVM, ActivityOtpVerificationBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_otp_verification
    }

    override fun createViewModel(): Class<out OptVerificationsVM> {
        return OptVerificationsVM::class.java
    }

    override fun setVM(binding: ActivityOtpVerificationBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        var data: JSONObject? = null
        val getOtp = Observer<ApiResponse> { apiResponse ->
            apiResponse?.let { response ->
                val json = JSONObject(response.data?.toDecrypt())
                getViewModel().otp.set(json.optString("otp"))
            }
        }
        if (intent.hasExtra(SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SIGNUP_DATA))
            getViewModel().getOTP(data.optString("mobile"), data.optString("email")).observe(this, getOtp)
        }
        btnSummit?.setOnClickListener {
            if (getViewModel().otp.get()?.length == 0) {
                simpleAlert("Please enter OTP") {
                    edtOtp?.selectAll()
                    edtOtp?.requestFocus()
                }
            } else {
                getViewModel().getOTP.value?.let { otp ->
                    otp.data?.let { it1 ->
                        getViewModel().verifyOTP(it1).observe(this, Observer {
                            data?.let {
                                getViewModel().onSignUp(it).observe(this, Observer { signUpResponse ->
                                    signUpResponse?.token?.let { it1 -> setLoginToken(it1) }
                                    if (intent.hasExtra(IS_FROM_ACCOUNT)) {
                                        setIsLogin(true)
                                        App.INSTANCE.isLoggedIn.value = true
                                        finish()
                                    } else {
                                        startActivity<HomeActivity>()
                                        finishAffinity()
                                    }
                                })
                            }
                        })
                    }
                }
            }
        }

        tvResendOtp?.setOnClickListener {
            //simpleAlert("OTP has benn resend successfully")
            data?.let { json ->
                getViewModel().getOTP(data.optString("mobile"), data.optString("email")).observe(this, getOtp)
            }
        }
    }
}
