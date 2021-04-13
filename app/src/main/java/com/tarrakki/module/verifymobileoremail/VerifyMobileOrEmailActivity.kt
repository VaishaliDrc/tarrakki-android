package com.tarrakki.module.verifymobileoremail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.ActivityEnterMobileNumberBinding
import com.tarrakki.databinding.ActivityVerifyMobileNumberBinding
import com.tarrakki.databinding.ActivityVerifyMobileOrEmailBinding
import com.tarrakki.fcm.onSignUpEventFire
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.register.SIGNUP_DATA
import com.tarrakki.module.register.SOACIAL_SIGNUP_DATA
import com.tarrakki.module.setpassword.SetPasswordActivity
import com.tarrakki.module.verifysocialmobilenumber.VerifyMobileNumberActivity
import com.tarrakki.module.verifysocialmobilenumber.VerifySocialMobileVM
import kotlinx.android.synthetic.main.activity_verify_mobile_number.*
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.*
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.btnContinue
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.etOTPFive
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.etOTPFour
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.etOTPOne
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.etOTPSix
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.etOTPThree
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.etOTPTwo
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.ivBack
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.tvEnterOTP
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.tvNotSpam
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.tvResendOtp
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.tvSendOtpViaCall
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.tvTimer
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.*

class VerifyMobileOrEmailActivity : CoreActivity<VerifyMobileOrEmailVM, ActivityVerifyMobileOrEmailBinding>() {

    var userOtp: String = ""
    override fun getLayout(): Int {
        return R.layout.activity_verify_mobile_or_email
    }

    override fun createViewModel(): Class<out VerifyMobileOrEmailVM> {
        return VerifyMobileOrEmailVM::class.java
    }

    override fun setVM(binding: ActivityVerifyMobileOrEmailBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun createReference() {

     //   setFocuseListener()
        setFocuseListenerNew()

        ivBack.setOnClickListener {
            finish()
        }

        getViewModel().resendOtpObserver.observe { second ->
            if (second <= 0) {
                tvTimer.visibility = View.INVISIBLE
                tvResendOtp.setTextColor(ContextCompat.getColor(this,R.color.light_black))
                tvSendOtpViaCall.setTextColor(ContextCompat.getColor(this,R.color.light_black))
                tvResendOtp.background =  ContextCompat.getDrawable(this,R.drawable.line_black)
                tvSendOtpViaCall.background =  ContextCompat.getDrawable(this,R.drawable.line_black)
                tvResendOtp.isClickable = true
                tvSendOtpViaCall.isClickable = true

            } else {
                tvTimer.visibility = View.VISIBLE
                if(second < 10){
                    tvTimer.text = "00:0$second"
                }else {
                    tvTimer.text = "00:$second"
                }
                tvResendOtp.setTextColor(ContextCompat.getColor(this,R.color.border_gray))
                tvSendOtpViaCall.setTextColor(ContextCompat.getColor(this,R.color.border_gray))
                tvResendOtp.background =  ContextCompat.getDrawable(this,R.drawable.line_gray_new)
                tvSendOtpViaCall.background =  ContextCompat.getDrawable(this,R.drawable.line_gray_new)
                tvResendOtp.isClickable = false
                tvSendOtpViaCall.isClickable = false
            }
        }

        getViewModel().startTimer(45)

        var data: JSONObject? = null

        if (intent.hasExtra(SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SIGNUP_DATA))
            getViewModel().email.set(data.optString("email"))
            getViewModel().mobile.set(data.optString("mobile"))
            getViewModel().hasEmail.set(data.optBoolean("is_email"))
            getViewModel().hasMobile.set(data.optBoolean("is_mobile"))
            getViewModel().isMobileVerified.set(data.optBoolean("is_mobile_verified"))
            getViewModel().isEmailVerified.set(data.optBoolean("is_email_verified"))
            getViewModel().otpId.set(data.optInt("otp_id"))
            getViewModel().firstName.set(data.optString("first_name"))
            getViewModel().lastName.set(data.optString("last_name"))
        }

        if (!getViewModel().isMobileVerified.get()!! && !getViewModel().isEmailVerified.get()!!) {
            if (getViewModel().hasEmail.get()!!) {
                tvOtpSent.visibility = View.VISIBLE
                tvSendOtpViaCall.visibility = View.GONE
                tvOtpSent.text = resources.getString(R.string.we_have_sent_otp, getViewModel().email.get())
                tvEnterOTP.text = resources.getString(R.string.enter_email_address_otp)
                tvNotSpam.visibility = View.GONE
            } else {
                tvOtpSent.visibility = View.GONE
                tvEnterOTP.text = resources.getString(R.string.enter_mobile_otp)
                tvSendOtpViaCall.visibility = View.VISIBLE
                tvNotSpam.visibility = View.VISIBLE
            }
        } else {
            if (getViewModel().isMobileVerified.get()!!) {
                tvOtpSent.visibility = View.VISIBLE
                tvSendOtpViaCall.visibility = View.GONE
                tvOtpSent.text = resources.getString(R.string.we_have_sent_otp, getViewModel().email.get())
                tvEnterOTP.text = resources.getString(R.string.enter_email_address_otp)
                tvNotSpam.setCompoundDrawables(null, null, null, null)
                tvNotSpam.text = resources.getString(R.string.your_registration_is_almost)
            } else {
                tvOtpSent.visibility = View.GONE
                tvEnterOTP.text = resources.getString(R.string.enter_mobile_otp)
                tvSendOtpViaCall.visibility = View.VISIBLE
                tvNotSpam.setCompoundDrawables(null, null, null, null)
                tvNotSpam.text = resources.getString(R.string.your_registration_is_almost)
            }
        }

        btnContinue.setOnClickListener {
            it.dismissKeyboard()
            if (isValidOTP()) {
                getViewModel().verifyEmailOrMobileOTP(userOtp).observe(this, Observer { verificationResponse ->
                    verificationResponse?.let {
                        val json = JsonObject()
                        if (it.isEmailVerified && it.isMobileVerified) {
                            json.addProperty("email", it.email)
                            json.addProperty("mobile", it.mobile)
                            json.addProperty("first_name", it.firstName)
                            json.addProperty("last_name", it.last_name)
                            val intent = Intent(this, SetPasswordActivity::class.java)
                            intent.putExtra(SIGNUP_DATA, json.toString())
                            startActivity(intent)
                            finish()
                        } else if (it.isEmailVerified || it.isMobileVerified) {
                            json.addProperty("email", it.email)
                            json.addProperty("mobile", it.mobile)
                            json.addProperty("is_email_verified", it.isEmailVerified)
                            json.addProperty("is_mobile_verified", it.isMobileVerified)
                            val intent = Intent(this, UserMobileEmailInputActivity::class.java)
                            intent.putExtra(SIGNUP_DATA, json.toString())
                            startActivity(intent)
                            finish()
                        }

                    }
                })
            }
        }

        tvResendOtp.setOnClickListener {
            if (!getViewModel().isEmailVerified.get()!! && !getViewModel().isMobileVerified.get()!!) {
                getViewModel().resendOTP()
            }else{
                getViewModel().reSendEmailOrMobileOTP()
            }
        }

        tvSendOtpViaCall.setOnClickListener {
            getViewModel().sendOTpViaCall()
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
                }else if(etOTPTwo.text.isEmpty()){
                    etOTPOne.requestFocus()
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
                }else if(etOTPThree.text.isEmpty()){
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

        etOTPFour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPFour.text.length == 1) {
                    etOTPFive.requestFocus()
                }else if(etOTPFour.text.isEmpty()){
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

        etOTPFive.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPFive.text.length == 1) {
                    etOTPSix.requestFocus()
                }else if(etOTPFive.text.isEmpty()){
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

        etOTPSix.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etOTPSix.text.length == 1) {
                    //  etOTPSix.clearFocus()

                }else if(etOTPSix.text.isEmpty()){
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


    }

    private fun setFocuseListenerNew() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
//first parameter is the current EditText and second parameter is next EditText
        etOTPOne.addTextChangedListener(VerifyMobileNumberActivity.GenericTextWatcher(etOTPOne, etOTPTwo))
        etOTPTwo.addTextChangedListener(VerifyMobileNumberActivity.GenericTextWatcher(etOTPTwo, etOTPThree))
        etOTPThree.addTextChangedListener(VerifyMobileNumberActivity.GenericTextWatcher(etOTPThree, etOTPFour))
        etOTPFour.addTextChangedListener(VerifyMobileNumberActivity.GenericTextWatcher(etOTPFour, etOTPFive))
        etOTPFive.addTextChangedListener(VerifyMobileNumberActivity.GenericTextWatcher(etOTPFive, etOTPSix))
        etOTPSix.addTextChangedListener(VerifyMobileNumberActivity.GenericTextWatcher(etOTPSix, null))

//GenericKeyEvent here works for deleting the element and to switch back to previous EditText
//first parameter is the current EditText and second parameter is previous EditText
        etOTPOne.setOnKeyListener(VerifyMobileNumberActivity.GenericKeyEvent(etOTPOne, null))
        etOTPTwo.setOnKeyListener(VerifyMobileNumberActivity.GenericKeyEvent(etOTPTwo, etOTPOne))
        etOTPThree.setOnKeyListener(VerifyMobileNumberActivity.GenericKeyEvent(etOTPThree, etOTPTwo))
        etOTPFour.setOnKeyListener(VerifyMobileNumberActivity.GenericKeyEvent(etOTPFour, etOTPThree))
        etOTPFive.setOnKeyListener(VerifyMobileNumberActivity.GenericKeyEvent(etOTPFive, etOTPFour))
        etOTPSix.setOnKeyListener(VerifyMobileNumberActivity.GenericKeyEvent(etOTPSix, etOTPFive))
    }

    class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.etOTPOne && currentView.text.isEmpty()) {
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
           //     R.id.etOTPSix -> if (text.length == 1) nextView!!.requestFocus()
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
