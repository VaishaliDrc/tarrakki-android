package com.tarrakki.module.upi

import android.os.Bundle
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentPaymentModeBinding
import com.tarrakki.databinding.FragmentUpiStepBinding
import org.supportcompact.CoreFragment


class UPIStepFragment : CoreFragment<UPIStepVM, FragmentUpiStepBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.upi_payment)


    override fun getLayout(): Int {
        return R.layout.fragment_upi_step
    }

    override fun createReference() {

    }

    override fun createViewModel(): Class<out UPIStepVM> {
        return UPIStepVM::class.java
    }

    override fun setVM(binding: FragmentUpiStepBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UPIStepFragment().apply { arguments = basket }
    }


}
