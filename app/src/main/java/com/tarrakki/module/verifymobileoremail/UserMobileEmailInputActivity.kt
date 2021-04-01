package com.tarrakki.module.verifymobileoremail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import com.tarrakki.R
import com.tarrakki.databinding.ActivityUserMobileEmailInputBinding
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.etMobile
import kotlinx.android.synthetic.main.activity_user_mobile_email_input.ivBack
import org.supportcompact.CoreActivity

class UserMobileEmailInputActivity : CoreActivity<VerifyMobileOrEmailVM, ActivityUserMobileEmailInputBinding>() {


    override fun getLayout(): Int {
        return R.layout.activity_user_mobile_email_input
    }

    override fun createViewModel(): Class<out VerifyMobileOrEmailVM> {
        return VerifyMobileOrEmailVM::class.java
    }

    override fun setVM(binding: ActivityUserMobileEmailInputBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setTextWatcher()
        ivBack.setOnClickListener {
            finish()
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