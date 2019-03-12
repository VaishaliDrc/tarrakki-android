package com.tarrakki.module.register

import android.arch.lifecycle.Observer
import android.content.Intent
import com.tarrakki.R
import com.tarrakki.databinding.ActivitySocialSignUpBinding
import com.tarrakki.module.otp.OtpVerificationActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.simpleAlert


class SocialSignUpActivity : CoreActivity<RegisterVM, ActivitySocialSignUpBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_social_sign_up
    }

    override fun createViewModel(): Class<out RegisterVM> {
        return RegisterVM::class.java
    }

    override fun setVM(binding: ActivitySocialSignUpBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        btnSignUp?.setOnClickListener {
            when {
                getViewModel().mobile.get()?.length == 0 -> simpleAlert("Please enter mobile number") {
                    edtMobile?.requestFocus()
                }
                getViewModel().mobile.get()?.length != 10 -> simpleAlert("Please enter valid mobile number") {
                    edtMobile?.requestFocus()
                }
                else -> {
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
}
