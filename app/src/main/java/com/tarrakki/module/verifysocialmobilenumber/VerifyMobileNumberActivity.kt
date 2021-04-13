package com.tarrakki.module.verifysocialmobilenumber

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
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
                tvResendOtp.setTextColor(ContextCompat.getColor(this, R.color.light_black))
                tvSendOtpViaCall.setTextColor(ContextCompat.getColor(this, R.color.light_black))
                tvResendOtp.background = ContextCompat.getDrawable(this, R.drawable.line_black)
                tvSendOtpViaCall.background = ContextCompat.getDrawable(this, R.drawable.line_black)
                tvResendOtp.isClickable = true
                tvSendOtpViaCall.isClickable = true

            } else {
                tvTimer.visibility = View.VISIBLE
                if (second < 10) {
                    tvTimer.text = "00:0$second"
                } else {
                    tvTimer.text = "00:$second"
                }
                tvResendOtp.setTextColor(ContextCompat.getColor(this, R.color.border_gray))
                tvSendOtpViaCall.setTextColor(ContextCompat.getColor(this, R.color.border_gray))
                tvResendOtp.background = ContextCompat.getDrawable(this, R.drawable.line_gray_new)
                tvSendOtpViaCall.background = ContextCompat.getDrawable(this, R.drawable.line_gray_new)
                tvResendOtp.isClickable = false
                tvSendOtpViaCall.isClickable = false
            }
        }

        getViewModel().startTimer(45)

        val getOtp = Observer<ApiResponse> { apiResponse ->
            apiResponse?.let { response ->
                getViewModel().startTimer(46)
                response.printResponse();
                response?.status?.message?.let {
                    simpleAlert(it)
                }
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
                if (data?.optString("mobile").toString().isNotEmpty())
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

    private fun setFocuseListener() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        etOTPOne.addTextChangedListener(GenericTextWatcher(etOTPOne, etOTPTwo))
        etOTPTwo.addTextChangedListener(GenericTextWatcher(etOTPTwo, etOTPThree))
        etOTPThree.addTextChangedListener(GenericTextWatcher(etOTPThree, etOTPFour))
        etOTPFour.addTextChangedListener(GenericTextWatcher(etOTPFour, etOTPFive))
        etOTPFive.addTextChangedListener(GenericTextWatcher(etOTPFive, etOTPSix))
        etOTPSix.addTextChangedListener(GenericTextWatcher(etOTPSix, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        etOTPOne.setOnKeyListener(GenericKeyEvent(etOTPOne, null))
        etOTPTwo.setOnKeyListener(GenericKeyEvent(etOTPTwo, etOTPOne))
        etOTPThree.setOnKeyListener(GenericKeyEvent(etOTPThree, etOTPTwo))
        etOTPFour.setOnKeyListener(GenericKeyEvent(etOTPFour, etOTPThree))
        etOTPFive.setOnKeyListener(GenericKeyEvent(etOTPFive, etOTPFour))
        etOTPSix.setOnKeyListener(GenericKeyEvent(etOTPSix, etOTPFive))
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


    @Subscribe(sticky = true)
    fun onReceive(apiResponse: ApiResponse) {
        if (getViewModel().getOTP.value == null) {
            getViewModel().getOTP.value = apiResponse
            EventBus.getDefault().removeStickyEvent(apiResponse)
        }
    }

    class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.etOTPOne && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }


    }

    class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) : TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.etOTPOne -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOTPTwo -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOTPThree -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOTPFour -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOTPFive -> if (text.length == 1) nextView!!.requestFocus()
                //R.id.etOTPSix -> if (text.length == 1) nextView!!.requestFocus()
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(
                arg0: CharSequence,
                arg1: Int,
                arg2: Int,
                arg3: Int
        ) { // TODO Auto-generated method stub
        }

        override fun onTextChanged(
                arg0: CharSequence,
                arg1: Int,
                arg2: Int,
                arg3: Int
        ) { // TODO Auto-generated method stub
        }

    }
}