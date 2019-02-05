package com.tarrakki.module.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.util.Patterns
import com.tarrakki.App
import com.tarrakki.IS_FROM_ACCOUNT
import com.tarrakki.R
import com.tarrakki.databinding.ActivityLoginBinding
import com.tarrakki.module.forgotpassword.ForgotPasswordActivity
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.*

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
            if (intent.hasExtra(IS_FROM_ACCOUNT)) {
                startActivity(Intent(this, RegisterActivity::class.java).apply {
                    putExtra(IS_FROM_ACCOUNT, true)
                })
            } else {
                startActivity<RegisterActivity>()
            }
        }

        btnLogin?.setOnClickListener {
            when {
                getViewModel().userName.get()?.length == 0 -> {
                    simpleAlert("Please enter email address") {
                        edtEmail?.requestFocus()
                    }
                }
                !Patterns.EMAIL_ADDRESS.matcher(getViewModel().userName.get()).matches() -> {
                    simpleAlert("Please enter valid email address") {
                        edtEmail?.requestFocus()
                    }
                }
                getViewModel().password.get()?.length == 0 -> {
                    simpleAlert("Please enter password") {
                        edtPassword?.requestFocus()
                    }
                }
                !getViewModel().password.isValidPassword() -> {
                    simpleAlert(getString(R.string.valid_password)) {
                        edtPassword?.requestFocus()
                        edtPassword?.setSelection(edtPassword.text.length)
                    }
                }
                else -> {
                    getViewModel().doLogin().observe(this, Observer { loginResponse ->
                        loginResponse?.let {
                            loginResponse.token?.let { it1 -> setLoginToken(it1) }
                            loginResponse.userId?.let { it1 -> setUserId(it1) }
                            loginResponse.email?.let { it1 -> setEmail(it1) }
                            loginResponse.mobile?.let { it1 -> setMobile(it1) }
                            if (!intent.hasExtra(IS_FROM_ACCOUNT)) {
                                startActivity<HomeActivity>()
                            }
                            setIsLogin(cbKeepMeSignIn.isChecked)
                            App.INSTANCE.isLoggedIn.value = true
                            finish()
                        }
                    })
                    /*if (!intent.hasExtra(IS_FROM_ACCOUNT)) {
                        startActivity<HomeActivity>()
                    }
                    setIsLogin(cbKeepMeSignIn.isChecked)
                    App.INSTANCE.isLoggedIn.value = true
                    finish()*/
                }
            }
        }
        App.INSTANCE.isLoggedIn.observe(this, Observer {
            it?.let { isLogin ->
                if (intent.hasExtra(IS_FROM_ACCOUNT) && isLogin)
                    finish()
            }
        })

    }
}
