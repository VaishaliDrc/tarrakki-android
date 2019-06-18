package com.tarrakki.module.forgotpassword

import android.arch.lifecycle.Observer
import android.content.Intent
import android.util.Patterns
import com.google.gson.JsonObject
import com.tarrakki.IS_FROM_FORGOT_PASSWORD
import com.tarrakki.R
import com.tarrakki.databinding.ActivityForgotPasswordBinding
import com.tarrakki.module.otp.OtpVerificationActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.simpleAlert

const val FORGOTPASSWORD_DATA = "forgotpassword_data"

class ForgotPasswordActivity : CoreActivity<ForgotPasswordVM, ActivityForgotPasswordBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_forgot_password
    }

    override fun createViewModel(): Class<out ForgotPasswordVM> {
        return ForgotPasswordVM::class.java
    }

    override fun setVM(binding: ActivityForgotPasswordBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        tvBack?.setOnClickListener {
            onBackPressed()
        }
        btnResetPassword?.setOnClickListener {
            if (getViewModel().email.get()?.length == 0) {
                simpleAlert(getString(R.string.alert_req_email)) {
                    edtEmail?.requestFocus()
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(getViewModel().email.get()).matches()) {
                simpleAlert(getString(R.string.alert_valid_email)) {
                    edtEmail?.requestFocus()
                    edtEmail?.selectAll()
                }
            } else {
                getViewModel().forgotPassword().observe(this, Observer { apiResponse ->
                    /*simpleAlert(apiResponse?.otp.toString()) {
                        edtEmail?.text?.clear()
                        finish()
                    }*/
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra(FORGOTPASSWORD_DATA, getOtpData(apiResponse?.otp, apiResponse?.otpId).toString())
                    if (getIntent().hasExtra(IS_FROM_FORGOT_PASSWORD)) {
                        intent.putExtra(IS_FROM_FORGOT_PASSWORD, true)
                    }
                    startActivity(intent)
                    finish()
                })
            }
        }
    }

    private fun getOtpData(otp: String?, otp_id: String?): JsonObject {
        val json = JsonObject()
        json.addProperty("otp", otp)
        json.addProperty("otp_id", otp_id)
        json.addProperty("email", "${getViewModel().email.get()}".toLowerCase().trim())
        return json
    }
}
