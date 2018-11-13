package com.tarrakki.module.login

import com.tarrakki.R
import com.tarrakki.databinding.ActivityLoginBinding
import com.tarrakki.module.forgotpassword.ForgotPasswordActivity
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.setIsLogin
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startActivity

class LoginActivity : CoreActivity<LoginVM, ActivityLoginBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun createViewModel(): Class<out LoginVM> {
        return LoginVM::class.java
    }

    override fun setVM(binding: ActivityLoginBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        tvForgotPassword?.setOnClickListener {
            startActivity<ForgotPasswordActivity>()
        }

        tvGetStarted?.setOnClickListener {
            startActivity<RegisterActivity>()
        }

        btnLogin?.setOnClickListener {
            when {
                getViewModel().userName.get()?.length == 0 -> {
                    simpleAlert("Please enter username") {
                        edtUserName?.requestFocus()
                    }
                }
                getViewModel().password.get()?.length == 0 -> {
                    simpleAlert("Please enter password") {
                        edtPassword?.requestFocus()
                    }
                }
                else -> {
                    startActivity<HomeActivity>()
                    setIsLogin(cbKeepMeSignIn.isChecked)
                    finish()
                }
            }
        }

    }
}
