package com.tarrakki.module.register

import android.arch.lifecycle.Observer
import android.content.Intent
import android.util.Patterns
import com.tarrakki.IS_FROM_INTRO
import com.tarrakki.R
import com.tarrakki.databinding.ActivityRegisterBinding
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.otp.OtpVerificationActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.isValidPassword
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startActivity

const val SIGNUP_DATA = "signup_data"
const val SIGNUP_OTP_DATA = "signup_otp_data"

class RegisterActivity : CoreActivity<RegisterVM, ActivityRegisterBinding>() {

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

        tvAlreadyHasAccount?.setOnClickListener {
            if (intent.hasExtra(IS_FROM_INTRO)) {
                startActivity<LoginActivity>()
                finish()
            } else {
                onBackPressed()
            }
        }

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
            } else if (getViewModel().mobile.get()?.length != 10) {
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
                simpleAlert("Please enter confirm password") {
                    edtPassword?.requestFocus()
                }
            } else if (getViewModel().confirmPassword.get() != getViewModel().password.get()) {
                simpleAlert("Password and confirm password miss match") {
                    edtConfirmPassword?.requestFocus()
                }
            } else if (cbTermsConditions?.isChecked == false) {
                simpleAlert("Please agree our Terms & Conditions.") {
                    edtConfirmPassword?.requestFocus()
                }
            } else {
                it.dismissKeyboard()
                getViewModel().getOTP(getViewModel().mobile.get(), getViewModel().email.get()).observe(this, Observer {
                    it?.let { it1 ->
                        val intent = Intent(this, OtpVerificationActivity::class.java)
                        intent.putExtra(SIGNUP_DATA, getViewModel().getSignUpData().toString())
                        startActivity(intent)
                        EventBus.getDefault().postSticky(it1)
                    }
                })
            }
        }
    }
}
