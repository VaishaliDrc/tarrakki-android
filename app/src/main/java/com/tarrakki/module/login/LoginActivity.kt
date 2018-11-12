package com.tarrakki.module.login

import com.tarrakki.R
import com.tarrakki.databinding.ActivityLoginBinding
import org.supportcompact.CoreActivity

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

    }
}
