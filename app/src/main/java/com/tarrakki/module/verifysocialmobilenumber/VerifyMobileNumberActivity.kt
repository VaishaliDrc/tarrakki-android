package com.tarrakki.module.verifysocialmobilenumber

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.api.model.printResponse
import com.tarrakki.api.model.toDecrypt
import com.tarrakki.databinding.ActivityVerifyMobileNumberBinding
import com.tarrakki.fcm.onSignUpEventFire
import com.tarrakki.module.forgotpassword.FORGOTPASSWORD_DATA
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LOGIN_DATA
import com.tarrakki.module.register.SIGNUP_DATA
import com.tarrakki.module.register.SOACIAL_SIGNUP_DATA
import com.tarrakki.module.resetPassword.ResetPasswordActivity
import com.tarrakki.module.setpassword.SetPasswordActivity
import kotlinx.android.synthetic.main.activity_new_login.*
import kotlinx.android.synthetic.main.activity_verify_mobile_number.*
import kotlinx.android.synthetic.main.activity_verify_mobile_number.btnContinue
import kotlinx.android.synthetic.main.activity_verify_mobile_number.etOTPFive
import kotlinx.android.synthetic.main.activity_verify_mobile_number.etOTPFour
import kotlinx.android.synthetic.main.activity_verify_mobile_number.etOTPOne
import kotlinx.android.synthetic.main.activity_verify_mobile_number.etOTPSix
import kotlinx.android.synthetic.main.activity_verify_mobile_number.etOTPThree
import kotlinx.android.synthetic.main.activity_verify_mobile_number.etOTPTwo
import kotlinx.android.synthetic.main.activity_verify_mobile_number.ivBack
import kotlinx.android.synthetic.main.activity_verify_mobile_number.tvEnterOTP
import kotlinx.android.synthetic.main.activity_verify_mobile_number.tvNotSpam
import kotlinx.android.synthetic.main.activity_verify_mobile_number.tvResendOtp
import kotlinx.android.synthetic.main.activity_verify_mobile_number.tvSendOtpViaCall
import kotlinx.android.synthetic.main.activity_verify_mobile_number.tvTimer
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.*

class VerifyMobileNumberActivity : CoreActivity<VerifySocialMobileVM, ActivityVerifyMobileNumberBinding>() {

    var data: JSONObject? = null
    var userOtp: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_verify_mobile_number
    }

    override fun createViewModel(): Class<out VerifySocialMobileVM> {
        return VerifySocialMobileVM::class.java
    }

    override fun setVM(binding: ActivityVerifyMobileNumberBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun createReference() {

        getViewModel().resendOtpObserver.observe { second ->
            if (second <= 0) {
                tvTimer.visibility = View.INVISIBLE
                tvResendOtp.isClickable = true
                tvSendOtpViaCall.isClickable = true

            } else {
                tvTimer.visibility = View.VISIBLE
                if(second < 10){
                    tvTimer.text = "00:0$second"
                }else {
                    tvTimer.text = "00:$second"
                }
                tvResendOtp.isClickable = false
                tvSendOtpViaCall.isClickable = false
            }
        }

        getViewModel().startTimer(45)

        val getOtp = Observer<ApiResponse> { apiResponse ->
            apiResponse?.let { response ->
                getViewModel().startTimer(46)
                response.printResponse();
                val json = JSONObject(response.data?.toDecrypt())
                //getViewModel().otp.set(json.optString("otp"))

            }
        }

        setFocuseListener()
        ivBack.setOnClickListener {
            finish()
        }
        if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SOACIAL_SIGNUP_DATA))
            data?.optString("mobile")?.let {
                if(data?.optString("mobile").toString().isNotEmpty())
                etMobile.setText("+91 ${data!!.optString("mobile")}")
            }
            etMobile.visibility = View.VISIBLE
            tvNotSpam.visibility = View.VISIBLE
            tvEnterOTP.text = resources.getString(R.string.enter_otp)
        }

        if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
            data = JSONObject(intent.getStringExtra(FORGOTPASSWORD_DATA))
            getViewModel().email.set(data?.optString("email").toString())
            getViewModel().otpId.set(data?.optString("otp_id").toString())
            etMobile.visibility = View.GONE
            tvNotSpam.visibility = View.GONE
            tvEnterOTP.text = resources.getString(R.string.enter_otp_sent_on_your_mobile)
            //getViewModel().otp.set(data?.optString("otp").toString())
        }

        btnContinue.setOnClickListener {
            it.dismissKeyboard()
            if (isValidOTP()) {
                if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
                    getViewModel().getOTP.value?.let { otp ->
                        otp.data?.let { it1 ->
                            getViewModel().verifySocialOTP(userOtp, it1).observe(this, Observer { signUpResponse ->
                                signUpResponse?.let {
                                    signUpResponse.token?.let { it1 -> setLoginToken(it1) }
                                    var bundle = Bundle()
                                    signUpResponse.userId?.let { it1 ->
                                        setUserId(it1)
//                                                    onSignUpEventFire(it1)
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
                                    setSocialLogin(true)
                                    setIsLogin(true)
                                    startActivity<HomeActivity>()
                                    finishAffinity()
                                }
                            })
                        }
                    }
                }
                if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
                    getViewModel().forgotPasswordVerifyOTP(userOtp, getViewModel().otpId.get()).observe(this,
                            Observer { apiResponse ->
                                val json = JsonObject()
                                json.addProperty("token", apiResponse?.token)
                                val intent = Intent(this, SetPasswordActivity::class.java)
                                intent.putExtra(FORGOTPASSWORD_DATA, json.toString())
                                startActivity(intent)
                                finish()
                            })
                }
            }

        }

        tvResendOtp.setOnClickListener {
            if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
                getViewModel().getOTP.value?.let { otp ->
                    otp.data?.let { it1 ->
                        getViewModel().getNewOTP(it1).observe(this, getOtp)
                        simpleAlert(getString(R.string.resend_otp_alert))
                    }
                }
            }
            if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
                getViewModel().forgotPasswordSendOTP(false).observe(this, Observer { apiResponse ->
                    getViewModel().otpId.set(apiResponse?.otpId.toString())
                    getViewModel().startTimer(46)
                    simpleAlert(getString(R.string.resend_otp_alert))
                })
            }
        }

        tvSendOtpViaCall.setOnClickListener {

            if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
                getViewModel().getOTP.value?.let { otp ->
                    otp.data?.let { it1 ->
                        getViewModel().getCallOTP(it1).observe(this, getOtp)
                        simpleAlert(getString(R.string.call_submitted))
                    }
                }
            }

            if (intent.hasExtra(FORGOTPASSWORD_DATA)) {
                getViewModel().forgotPasswordSendOTP(true).observe(this, Observer { apiResponse ->
                    getViewModel().otpId.set(apiResponse?.otpId.toString())
                    getViewModel().startTimer(46)
                    simpleAlert(getString(R.string.call_submitted))
                })
            }

        }
    }

    private fun isValidOTP(): Boolean {
        if ((etOTPOne.text.toString() + etOTPTwo.text.toString() + etOTPThree.text.toString() + etOTPFour.text.toString() + etOTPFive.text.toString() + etOTPSix.text.toString()).length == 6) {
            userOtp = (etOTPOne.text.toString() + etOTPTwo.text.toString() + etOTPThree.text.toString() + etOTPFour.text.toString() + etOTPFive.text.toString() + etOTPSix.text.toString())
            return true
        } else {
            simpleAlert(getString(R.string.alert_req_otp))
            return false
        }

    }

    private fun setFocuseListener() {

        etOTPOne.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPOne.text.length == 1) {
                    etOTPTwo.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                       count: Int) {
            }
        })
        etOTPTwo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPTwo.text.length == 1) {
                    etOTPThree.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                       count: Int) {
            }
        })

        etOTPThree.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPThree.text.length == 1) {
                    etOTPFour.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                       count: Int) {
            }
        })

        etOTPFour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPFour.text.length == 1) {
                    etOTPFive.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                       count: Int) {
            }
        })

        etOTPFive.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPFive.text.length == 1) {
                    etOTPSix.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                       count: Int) {
            }
        })

        etOTPSix.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPFive.text.length == 1) {
                    //  etOTPSix.clearFocus()

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                       count: Int) {
            }
        })


    }

    @Subscribe(sticky = true)
    fun onReceive(apiResponse: ApiResponse) {
        if (getViewModel().getOTP.value == null) {
            getViewModel().getOTP.value = apiResponse
            EventBus.getDefault().removeStickyEvent(apiResponse)
        }
    }
}