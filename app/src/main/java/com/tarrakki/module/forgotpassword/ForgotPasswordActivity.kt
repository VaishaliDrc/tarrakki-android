package com.tarrakki.module.forgotpassword

import android.util.Patterns
import com.tarrakki.R
import com.tarrakki.databinding.ActivityForgotPasswordBinding
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.simpleAlert

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
                simpleAlert("Please enter email address") {
                    edtEmail?.requestFocus()
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(getViewModel().email.get()).matches()) {
                simpleAlert("Please enter valid email address") {
                    edtEmail?.requestFocus()
                    edtEmail?.selectAll()
                }
            } else {
                simpleAlert("Reset password link has been sent to your email address") {
                    edtEmail?.text?.clear()
                }
            }
        }
    }
}
