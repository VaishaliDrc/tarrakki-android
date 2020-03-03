package com.tarrakki.module.ekyc

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentEKYCRemainingDetailsBinding
import com.tarrakki.module.birth_certificate.UploadDOBCertiFragment
import kotlinx.android.synthetic.main.fragment_e_k_y_c_remaining_details.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.showCustomListDialog
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [EKYCRemainingDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EKYCRemainingDetailsFragment : CoreFragment<EKYCConfirmationVM, FragmentEKYCRemainingDetailsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.e_kyc)

    override fun getLayout(): Int {
        return R.layout.fragment_e_k_y_c_remaining_details
    }

    override fun createViewModel(): Class<out EKYCConfirmationVM> {
        return EKYCConfirmationVM::class.java
    }

    override fun setVM(binding: FragmentEKYCRemainingDetailsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnContinue?.setOnClickListener {
            startFragment(UploadDOBCertiFragment.newInstance(), R.id.frmContainer)
            getViewModel().kycData?.let { postSticky(it) }
        }

        edtSourceIncome?.setOnClickListener {
            context?.showCustomListDialog(R.string.source_of_income, getViewModel().sourcesOfIncomes) { item ->
                getViewModel().sourceOfIncome.set(item.value)
                getViewModel().kycData?.sourceOfIncome = item.key
            }
        }
        edtIncomeSlab?.setOnClickListener {
            context?.showCustomListDialog(R.string.income_slab, getViewModel().incomeSlabs) { item ->
                getViewModel().TAXSlab.set(item.value)
                getViewModel().kycData?.taxSlab = item.key
            }
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData == null) {
            getViewModel().kycData = kycData
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment EKYCRemainingDetailsFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = EKYCRemainingDetailsFragment().apply { arguments = basket }
    }
}
