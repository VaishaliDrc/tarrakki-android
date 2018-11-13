package com.tarrakki.module.register

import android.util.Patterns
import com.tarrakki.R
import com.tarrakki.databinding.ActivityRegisterBinding
import kotlinx.android.synthetic.main.activity_register.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.simpleAlert

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
            onBackPressed()
        }

        btnSignUp?.setOnClickListener {

            if (getViewModel().email.get()?.length == 0) {
                simpleAlert("Please enter email address") {
                    edtEmail.requestFocus()
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(getViewModel().email.get()).matches()) {
                simpleAlert("Please enter valid email address") {
                    edtEmail?.requestFocus()
                    edtEmail?.selectAll()
                }
            } else if (getViewModel().mobile.get()?.length == 0) {
                simpleAlert("Please enter mobile number") {
                    edtEmail?.requestFocus()
                    edtEmail?.selectAll()
                }
            } else if (getViewModel().password.get()?.length == 0) {
                simpleAlert("Please enter password") {
                    edtPassword?.requestFocus()
                }
            } else if (getViewModel().confirmPassword.get()?.length == 0) {
                simpleAlert("Please enter confirm password") {
                    edtPassword?.requestFocus()
                }
            } else if (getViewModel().confirmPassword.get() == getViewModel().password.get()) {
                simpleAlert("Password and confirm password miss match") {
                    edtPassword?.requestFocus()
                }
            }

        }

    }
}
