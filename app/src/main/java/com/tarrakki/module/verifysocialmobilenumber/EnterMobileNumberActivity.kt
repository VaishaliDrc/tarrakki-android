package com.tarrakki.module.verifysocialmobilenumber

import android.content.Intent
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.databinding.ActivityEnterMobileNumberBinding
import com.tarrakki.getOrganizationCode
import com.tarrakki.isTarrakki
import com.tarrakki.module.register.SOACIAL_SIGNUP_DATA
import kotlinx.android.synthetic.main.activity_enter_mobile_number.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.dismissKeyboard
import org.supportcompact.ktx.isValidMobile
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startActivity


class EnterMobileNumberActivity : CoreActivity<VerifySocialMobileVM, ActivityEnterMobileNumberBinding>() {


    override fun getLayout(): Int {
        return R.layout.activity_enter_mobile_number
    }

    override fun createViewModel(): Class<out VerifySocialMobileVM> {
        return VerifySocialMobileVM::class.java
    }

    override fun setVM(binding: ActivityEnterMobileNumberBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        setTextWatcher()
        ivBack.setOnClickListener {
            finish()
        }
        var data: JSONObject? = null

        if (intent.hasExtra(SOACIAL_SIGNUP_DATA)) {
            data = JSONObject(intent.getStringExtra(SOACIAL_SIGNUP_DATA))
         //   hasEmail = data.optString("email").isNotEmpty()
        }



        btnContinue.setOnClickListener {
            it.dismissKeyboard()
            if (etMobile.text.isNotEmpty() && etMobile.text.substring(4).isValidMobile()){
                data?.let {
                    it.put("mobile", etMobile.text.substring(4) )
                    it.put("promocode", "")
                    it.put("organization", BuildConfig.FLAVOR.isTarrakki().getOrganizationCode())
                    getViewModel().socialSignUp(it).observe(this, Observer {
                        it?.let { it1 ->
                            val intent = Intent(this, VerifyMobileNumberActivity::class.java)
                            intent.putExtra(SOACIAL_SIGNUP_DATA, data.toString())
                            startActivity(intent)
                            EventBus.getDefault().postSticky(it1)
                            finish()

                        }
                    })
                }


            }else{
                simpleAlert(getString(R.string.pls_enter_valid_indian_mobile_number))
            }
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