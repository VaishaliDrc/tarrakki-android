package com.tarrakki.module.otp

import android.arch.lifecycle.Observer
import android.content.Intent
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.toDecrypt
import com.tarrakki.databinding.ActivityOtpVerificationBinding
import com.tarrakki.module.forgotpassword.FORGOTPASSWORD_DATA
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.myprofile.PROFILE_EMAIL_DATA
import com.tarrakki.module.myprofile.PROFILE_MOBILE_DATA
import com.tarrakki.module.register.SIGNUP_DATA
import com.tarrakki.module.register.SOACIAL_SIGNUP_DATA
import com.tarrakki.module.resetPassword.ResetPasswordActivity
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.events.Event
import org.supportcompact.ktx.*

class OtpVerificationActivity : CoreActivity<OptVerificationsVM, ActivityOtpVerificationBinding>() {

    var data: JSONObject? = null
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
        val getOtp = Observer<ApiResponse> { apiResponse ->
            apiResponse?.let { response ->
                val json = JSONObject(response.data?.toDecrypt())
                //getViewModel().otp.set(json.optString("otp"))
                edtOtp?.length()?.let { edtOtp?.setSelection(0, it) }
            }
        }
        getViewModel().getOTP.observe(this, getOtp)
        if (intent.hasExtra(SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SIGNUP_DATA))
        }
        if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SOACIAL_SIGNUP_DATA))
        }
        if (intent.hasExtra(PROFILE_EMAIL_DATA)) {
            data = JSONObject(intent.getStringExtra(PROFILE_EMAIL_DATA))
        }
        if (intent.hasExtra(PROFILE_MOBILE_DATA)) {
            data = JSONObject(intent.getStringExtra(PROFILE_MOBILE_DATA))
        }
        if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
            data = JSONObject(intent.getStringExtra(FORGOTPASSWORD_DATA))
            getViewModel().email.set(data?.optString("email").toString())
            getViewModel().otpId.set(data?.optString("otp_id").toString())
            //getViewModel().otp.set(data?.optString("otp").toString())
        }
        btnSummit?.setOnClickListener {
            if (getViewModel().otp.get()?.length == 0) {
                simpleAlert(getString(R.string.alert_req_otp)) {
                    edtOtp?.selectAll()
                    edtOtp?.requestFocus()
                }
            } else {
                if (intent.hasExtra(SIGNUP_DATA)) {
                    getViewModel().getOTP.value?.let { otp ->
                        otp.data?.let { it1 ->
                            getViewModel().verifyOTP(it1).observe(this, Observer {
                                data?.let {
                                    if (intent.hasExtra(SIGNUP_DATA)) {
                                        getViewModel().onSignUp(it).observe(this, Observer { signUpResponse ->
                                            signUpResponse?.let {
                                                signUpResponse.token?.let { it1 -> setLoginToken(it1) }
                                                signUpResponse.userId?.let { it1 -> setUserId(it1) }
                                                signUpResponse.email?.let { it1 -> setEmail(it1) }
                                                signUpResponse.mobile?.let { it1 -> setMobile(it1) }
                                                setIsLogin(true)
                                                startActivity<HomeActivity>()
                                                finishAffinity()
                                            }
                                        })
                                    }
                                }
                            })
                        }
                    }
                }

                if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
                    getViewModel().getOTP.value?.let { otp ->
                        otp.data?.let { it1 ->
                            getViewModel().verifySocialOTP(it1).observe(this, Observer { signUpResponse ->
                                signUpResponse?.let {
                                    signUpResponse.token?.let { it1 -> setLoginToken(it1) }
                                    signUpResponse.userId?.let { it1 -> setUserId(it1) }
                                    signUpResponse.email?.let { it1 -> setEmail(it1) }
                                    signUpResponse.mobile?.let { it1 -> setMobile(it1) }
                                    setIsLogin(true)
                                    startActivity<HomeActivity>()
                                    finishAffinity()
                                }
                            })
                        }
                    }
                }

                if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
                    getViewModel().forgotPasswordVerifyOTP(getViewModel().otp.get()
                            , getViewModel().otpId.get()).observe(this,
                            Observer { apiResponse ->
                                val intent = Intent(this, ResetPasswordActivity::class.java)
                                intent.putExtra("token", apiResponse?.token)
                                startActivity(intent)
                                finish()
                            })
                }

                if (intent.hasExtra(PROFILE_MOBILE_DATA)) {
                    getViewModel().getOTP.value?.let { otp ->
                        otp.data?.let { it1 ->
                            getViewModel().verifyOTP(it1).observe(this, Observer {
                                data?.let {
                                    if (intent.hasExtra(PROFILE_MOBILE_DATA)) {
                                        finish()
                                        postSticky(Event.ISMOBILEVERIFIED)
                                    }
                                }
                            })
                        }
                    }
                }

                if (intent.hasExtra(PROFILE_EMAIL_DATA)) {
                    getViewModel().getOTP.value?.let { otp ->
                        otp.data?.let { it1 ->
                            getViewModel().verifyOTP(it1).observe(this, Observer {
                                data?.let {
                                    if (intent.hasExtra(PROFILE_EMAIL_DATA)) {
                                        finish()
                                        postSticky(Event.ISEMAILVERIFIED)
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }

        tvResendOtp?.setOnClickListener {
            //simpleAlert("OTP has benn resend successfully")
            if (intent.hasExtra(SIGNUP_DATA)) {
                data?.let { json ->
                    getViewModel().getOTP(data?.optString("mobile"), data?.optString("email")).observe(this, getOtp)
                }
            }

            if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
                getViewModel().getOTP.value?.let { otp ->
                    otp.data?.let { it1 ->
                        getViewModel().getNewOTP(it1).observe(this, getOtp)
                    }
                }
            }

            if (intent.hasExtra(PROFILE_MOBILE_DATA)) {
                data?.let { json ->
                    getViewModel().getDataOTP(json.toString()).observe(this, getOtp)
                }
            }

            if (intent.hasExtra(PROFILE_EMAIL_DATA)) {
                data?.let { json ->
                    getViewModel().getDataOTP(json.toString()).observe(this, getOtp)
                }
            }

            if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
                getViewModel().forgotPasswordSendOTP().observe(this, Observer { apiResponse ->
                    getViewModel().otpId.set(apiResponse?.otpId.toString())
                    //getViewModel().otp.set(apiResponse?.otp.toString())
                })
            }
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(apiResponse: ApiResponse) {
        getViewModel().getOTP.value = apiResponse
        EventBus.getDefault().removeStickyEvent(apiResponse)
    }

    override fun onBackPressed() {
        if (intent.hasExtra(PROFILE_MOBILE_DATA)) {
            postSticky(Event.ISMOBILEVERIFIEDBACK)
        }
        if (intent.hasExtra(PROFILE_EMAIL_DATA)) {
            postSticky(Event.ISEMAILVERIFIEDBACK)
        }
        super.onBackPressed()
    }

}
