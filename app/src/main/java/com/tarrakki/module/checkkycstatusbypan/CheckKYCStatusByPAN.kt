package com.tarrakki.module.checkkycstatusbypan

import com.tarrakki.R
import com.tarrakki.databinding.ActivityCheckKycStatusByPanBinding
import com.tarrakki.databinding.ActivityEnterMobileNumberBinding
import com.tarrakki.module.home.HomeActivity
import kotlinx.android.synthetic.main.activity_check_kyc_status_by_pan.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.startActivity

class CheckKYCStatusByPAN : CoreActivity<CheckKYCVM, ActivityCheckKycStatusByPanBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_check_kyc_status_by_pan
    }

    override fun createViewModel(): Class<out CheckKYCVM> {
        return CheckKYCVM::class.java
    }

    override fun setVM(binding: ActivityCheckKycStatusByPanBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        ivBack.setOnClickListener {
            finish()
        }
        tvSkip.setOnClickListener {
            startActivity<HomeActivity>()
            finish()
        }

    }

}