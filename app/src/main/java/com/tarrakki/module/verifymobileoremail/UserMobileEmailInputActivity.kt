package com.tarrakki.module.verifymobileoremail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.tarrakki.R
import com.tarrakki.databinding.ActivityUserMobileEmailInputBinding
import com.tarrakki.module.register.SIGNUP_DATA
import kotlinx.android.synthetic.main.activity_enter_mobile_number.*
import kotlinx.android.synthetic.main.activity_new_login.*
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.*
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.btnContinue
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.etEmail
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.etMobile
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.ivBack
import kotlinx.android.synthetic.main.activity_verify_mobile_or_email.*
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.isEmail
import org.supportcompact.ktx.isValidMobile
import org.supportcompact.ktx.simpleAlert

class UserMobileEmailInputActivity : CoreActivity<UserMobileEmailInputVM, ActivityUserMobileEmailInputBinding>() {


    override fun getLayout(): Int {
        return R.layout.activity_user_mobile_email_input
    }

    override fun createViewModel(): Class<out UserMobileEmailInputVM> {
        return UserMobileEmailInputVM::class.java
    }

    override fun setVM(binding: ActivityUserMobileEmailInputBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun createReference() {
        setTextWatcher()
        ivBack.setOnClickListener {
            finish()
        }
        var data: JSONObject? = null
        if (intent.hasExtra(SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SIGNUP_DATA))
            getViewModel().email.set(data.optString("email"))
            getViewModel().mobile.set(data.optString("mobile"))
            getViewModel().isMobileVerified.set(data.optBoolean("is_mobile_verified"))
            getViewModel().isEmailVerified.set(data.optBoolean("is_email_verified"))
        }

        if(getViewModel().isMobileVerified.get()!!){
            etMobile.setText("+91 ${getViewModel().mobile.get()}")
            etMobile.setTextColor(ContextCompat.getColor(this,R.color.auto_cancel))
            etMobile.isClickable = false
            etMobile.isFocusable = false
            etMobile.isEnabled = false
        }else{
            etEmail.setText(getViewModel().email.get())
            etEmail.setTextColor(ContextCompat.getColor(this,R.color.auto_cancel))
            etEmail.isClickable = false
            etEmail.isFocusable = false
            etEmail.isEnabled = false
        }

        btnContinue.setOnClickListener {
            it.dismissKeyboard()
            if(isValidate()){
                getViewModel().sendEmailOrMobileOTP().observe(this, Observer { otpResponse ->
                    otpResponse?.let {
                        it.otpId?.let {
                            val json = JsonObject()
                            json.addProperty("is_mobile", getViewModel().isMobileVerified.get())
                            json.addProperty("is_email",getViewModel().isEmailVerified.get())
                            json.addProperty("email", getViewModel().email.get())
                            json.addProperty("mobile", getViewModel().mobile.get())
                            json.addProperty("is_email_verified", getViewModel().isEmailVerified.get())
                            json.addProperty("is_mobile_verified", getViewModel().isMobileVerified.get())
                            json.addProperty("first_name", getViewModel().firstName.get())
                            json.addProperty("last_name", getViewModel().lastName.get())
                            json.addProperty("otp_id", it)
                            val intent = Intent(this, VerifyMobileOrEmailActivity::class.java)
                            intent.putExtra(SIGNUP_DATA, json.toString())
                            startActivity(intent)
                            finish()
                        }
                    }
                })
            }
        }


    }

    private fun isValidate(): Boolean {
        if(etFirstName.text.toString().trim().isEmpty()){
            simpleAlert(getString(R.string.pls_enter_first_name))
            etFirstName.requestFocus()
            return false
        }else if(etLastName.text.toString().trim().isEmpty()) {
            simpleAlert(getString(R.string.pls_enter_last_name))
            etLastName.requestFocus()
            return false
        }else if (etEmail.text.toString().isEmpty()){
            simpleAlert(getString(R.string.pls_enter_email_address))
            etEmail.requestFocus()
            return false
        }else if (!etEmail.text.toString().isEmail()){
            simpleAlert(getString(R.string.pls_enter_valid_email_address))
            etEmail.requestFocus()
            return false
        }else if (etMobile.text.toString().isEmpty()){
            simpleAlert(getString(R.string.pls_enter_mobile_number))
            etMobile.requestFocus()
            return false
        }else if (!etMobile.text.trim().substring(4).isValidMobile()){
            simpleAlert(getString(R.string.pls_enter_valid_indian_mobile_number))
            etMobile.requestFocus()
            return false
        }else{
            getViewModel().email.set(etEmail.text.trim().toString())
            getViewModel().mobile.set(etMobile.text.trim().substring(4))
            getViewModel().firstName.set(etFirstName.text.trim().toString())
            getViewModel().lastName.set(etLastName.text.trim().toString())
            return true
        }

    }

    private fun setTextWatcher() {
        val mobileTextWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text_value: String = etMobile.text.toString().trim()
                if (text_value.equals("+91", ignoreCase = true)) {
                    etMobile.setText("")
                } else {
                    if (!text_value.startsWith("+91") && text_value.length > 0) {
                        etMobile.setText("+91 " + s.toString())
                        Selection.setSelection(etMobile.text, etMobile.text.length)
                    }
                }
            }

        }
        etMobile.addTextChangedListener(mobileTextWatcher)

    }

}