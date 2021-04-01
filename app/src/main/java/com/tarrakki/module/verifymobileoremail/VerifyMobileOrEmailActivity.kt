package com.tarrakki.module.verifymobileoremail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.ActivityEnterMobileNumberBinding
import com.tarrakki.databinding.ActivityVerifyMobileNumberBinding
import com.tarrakki.databinding.ActivityVerifyMobileOrEmailBinding
import com.tarrakki.fcm.onSignUpEventFire
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.register.SIGNUP_DATA
import com.tarrakki.module.register.SOACIAL_SIGNUP_DATA
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

        override fun createReference() {

        setFocuseListener()

        ivBack.setOnClickListener {
            finish()
        }

        getViewModel().resendOtpObserver.observe { second ->
            if (second <= 0) {
                tvTimer.visibility = View.INVISIBLE
                tvResendOtp.isClickable = true
                tvSendOtpViaCall.isClickable = true

            } else {
                tvTimer.visibility = View.VISIBLE
                tvTimer.text = "00:$second"
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
        }

        if (!getViewModel().isMobileVerified.get()!! && !getViewModel().isMobileVerified.get()!!) {
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
                tvOtpSent.text = resources.getString(R.string.we_have_sent_otp, getViewModel().email)
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
                        if(it.isEmailVerified || it.isMobileVerified){
                            startActivity<UserMobileEmailInputActivity>()
                            finish()
                        }

                    }
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


}
