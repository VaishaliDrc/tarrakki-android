package com.tarrakki.module.ekyc


import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationBBinding
import kotlinx.android.synthetic.main.fragment_kycregistration_b.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.isEmpty
import org.supportcompact.ktx.showListDialog
import org.supportcompact.ktx.simpleAlert

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationBFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class KYCRegistrationBFragment : CoreFragment<KYCRegistrationBVM, FragmentKycregistrationBBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_kycregistration_b
    }

    override fun createViewModel(): Class<out KYCRegistrationBVM> {
        return KYCRegistrationBVM::class.java
    }

    override fun setVM(binding: FragmentKycregistrationBBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        switchOnOff?.setOnCheckedChangeListener { buttonView, isChecked ->
            getViewModel().iCertify.set(isChecked)
        }
        edtSourceIncome?.setOnClickListener {
            context?.showListDialog(R.string.source_of_income, R.array.income_source) { item ->
                getViewModel().sourceOfIncome.set(item)
            }
        }
        edtIncomeSlab?.setOnClickListener {
            context?.showListDialog(R.string.income_slab, R.array.income_slab) { item ->
                getViewModel().TAXSlab.set(item)
            }
        }
        edtIssue?.setOnClickListener {
            showCountry(getViewModel().issueByA)
        }
        edtIssue1?.setOnClickListener {
            showCountry(getViewModel().issueByB)
        }
        edtIssue2?.setOnClickListener {
            showCountry(getViewModel().issueByC)
        }
        btnLogout?.setOnClickListener {
            if (isValid()) {

            }
        }
    }

    private fun showCountry(item: ObservableField<String>) {
        context?.showListDialog(R.string.select_country, R.array.countries) { country ->
            item.set(country)
        }
    }


    private fun isValid(): Boolean {
        return when {
            getViewModel().PANName.isEmpty() -> {
                context?.simpleAlert("Please enter PAN name")
                false
            }
            getViewModel().sourceOfIncome.isEmpty() -> {
                context?.simpleAlert("Please select source of income")
                false
            }
            getViewModel().TAXSlab.isEmpty() -> {
                context?.simpleAlert("Please select income slab")
                false
            }
            !switchOnOff.isChecked &&
                    getViewModel().TINNumberA.isEmpty() &&
                    getViewModel().TINNumberB.isEmpty() &&
                    getViewModel().TINNumberC.isEmpty() -> {
                context?.simpleAlert("Please enter TIN number")
                false
            }
            !switchOnOff.isChecked && getViewModel().issueByA.isEmpty() &&
                    getViewModel().issueByB.isEmpty() &&
                    getViewModel().issueByC.isEmpty() -> {
                context?.simpleAlert("Please select country of issue")
                false
            }
            else -> true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment KYCRegistrationBFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = KYCRegistrationBFragment().apply { arguments = basket }
    }
}
