package com.tarrakki.module.register

import android.arch.lifecycle.Observer
import android.content.Intent
import com.tarrakki.R
import com.tarrakki.databinding.ActivitySocialSignUpBinding
import com.tarrakki.module.otp.OtpVerificationActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.simpleAlert

const val SOACIAL_SIGNUP_DATA = "social_signup_data"

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
        var data: JSONObject? = null
        if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SOACIAL_SIGNUP_DATA))
        }
        btnSignUp?.setOnClickListener {
            when {
                getViewModel().mobile.get()?.length == 0 -> simpleAlert("Please enter mobile number") {
                    edtMobile?.requestFocus()
                }
                getViewModel().mobile.get()?.length != 10 -> simpleAlert("Please enter valid mobile number") {
                    edtMobile?.requestFocus()
                }
                cbTermsConditions?.isChecked == false -> {
                    simpleAlert("Please agree our Terms & Conditions.") {
                        edtConfirmPassword?.requestFocus()
                    }
                }
                else -> {
                    it.dismissKeyboard()
                    data?.let {
                        it.put("mobile", getViewModel().mobile.get())
                        getViewModel().socialSignUp(it).observe(this, Observer {
                            it?.let { it1 ->
                                val intent = Intent(this, OtpVerificationActivity::class.java)
                                intent.putExtra(SOACIAL_SIGNUP_DATA, data.toString())
                                startActivity(intent)
                                EventBus.getDefault().postSticky(it1)
                            }
                        })
                    }
                }
            }
        }
    }
}
