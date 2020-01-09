package com.tarrakki.module.register

import androidx.lifecycle.Observer
import android.content.Intent
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.ActivitySocialSignUpBinding
import com.tarrakki.module.otp.OtpVerificationActivity
import com.tarrakki.module.webviewActivity.CMSPagesActivity
import kotlinx.android.synthetic.main.activity_social_sign_up.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.*

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
        var hasEmail = false
        if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SOACIAL_SIGNUP_DATA))
            hasEmail = data.optString("email").isNotEmpty()
            edtEmail?.visibility = if (hasEmail) View.GONE else View.VISIBLE
        }

        val termsAndCondditionClickSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                startActivity<CMSPagesActivity>()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                color(R.color.colorAccent).let { ds.color = it }
            }
        }

        cbTermsConditions?.makeLinks(arrayOf("Terms and Conditions"), arrayOf(termsAndCondditionClickSpan))

        btnSignUp?.setOnClickListener {
            when {

                !hasEmail && getViewModel().email.isEmpty() -> {
                    simpleAlert(getString(R.string.pls_enter_email_address)) {
                        edtEmail.requestFocus()
                    }
                }
                !hasEmail && !getViewModel().email.isEmail() -> {
                    simpleAlert(getString(R.string.pls_enter_valid_email_address)) {
                        edtEmail?.requestFocus()
                    }
                }
                getViewModel().mobile.get()?.length == 0 -> simpleAlert(getString(R.string.pls_enter_mobile_number)) {
                    edtMobile?.requestFocus()
                }
                !getViewModel().mobile.isValidMobile() -> simpleAlert(getString(R.string.pls_enter_valid_indian_mobile_number)) {
                    edtMobile?.requestFocus()
                }
                cbTermsConditions?.isChecked == false -> {
                    simpleAlert(getString(R.string.alert_req_terms))
                }
                else -> {
                    it.dismissKeyboard()
                    data?.let {
                        if (!hasEmail) {
                            it.put("email", "${getViewModel().email.get()}".toLowerCase().trim())
                        }
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
