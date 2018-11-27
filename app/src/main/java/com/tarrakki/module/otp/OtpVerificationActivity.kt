package com.tarrakki.module.otp

import com.tarrakki.App
import com.tarrakki.IS_FROM_ACCOUNT
import com.tarrakki.R
import com.tarrakki.databinding.ActivityOtpVerificationBinding
import com.tarrakki.module.home.HomeActivity
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.setIsLogin
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

        btnSummit?.setOnClickListener {
            if (getViewModel().otp.get()?.length == 0) {
                simpleAlert("Please enter OTP") {
                    edtOtp?.selectAll()
                    edtOtp?.requestFocus()
                }
            } else {
                if (intent.hasExtra(IS_FROM_ACCOUNT)) {
                    setIsLogin(true)
                    App.INSTANCE.isLoggedIn.value = true
                    finish()
                } else {
                    startActivity<HomeActivity>()
                    finishAffinity()
                }
            }
        }

        tvResendOtp?.setOnClickListener {
            simpleAlert("OTP has benn resend successfully")
        }
    }
}
