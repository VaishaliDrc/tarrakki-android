package com.tarrakki.module.ekyc


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.IS_FROM_COMLETE_REGISTRATION
import com.tarrakki.R
import com.tarrakki.api.model.Country
import com.tarrakki.api.model.parseArray
import com.tarrakki.databinding.FragmentKycregistrationBBinding
import com.tarrakki.module.bankaccount.BankAccountsFragment
import kotlinx.android.synthetic.main.fragment_kycregistration_b.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.isEmpty
import org.supportcompact.ktx.showCustomListDialog
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationBFragment.newInstance] factory method to
 * create an instance of this fragment.
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
        val countryJSON = resources.openRawResource(R.raw.country).bufferedReader().use { it.readText() }
        val countries = countryJSON.parseArray<ArrayList<Country>>()
        getViewModel().kycData.observe(this, Observer {
            it?.let { kycData ->
                getBinding().kycData = kycData
                getBinding().executePendingBindings()
                edtCountry?.text = countries?.firstOrNull { it.code == kycData.birthCountry }?.name
                getViewModel().sourceOfIncome.set(getViewModel().sourcesOfIncomes.firstOrNull { it.key == kycData.sourceOfIncome }?.value)
                getViewModel().TAXSlab.set(getViewModel().incomeSlabs.firstOrNull { it.key == kycData.taxSlab }?.value)
            }
        })
        switchOnOff?.setOnCheckedChangeListener { buttonView, isChecked ->
            getViewModel().iCertify.set(isChecked)
        }
        edtSourceIncome?.setOnClickListener {
            context?.showCustomListDialog(R.string.source_of_income, getViewModel().sourcesOfIncomes) { item ->
                getViewModel().sourceOfIncome.set(item.value)
                getViewModel().kycData.value?.sourceOfIncome = item.key
            }
        }
        edtIncomeSlab?.setOnClickListener {
            context?.showCustomListDialog(R.string.income_slab, getViewModel().incomeSlabs) { item ->
                getViewModel().TAXSlab.set(item.value)
                getViewModel().kycData.value?.taxSlab = item.key
            }
        }
        edtCountry?.setOnClickListener {
            countries?.let {
                context?.showCustomListDialog(R.string.select_country, countries) { item: Country ->
                    getViewModel().kycData.value?.birthCountry = item.code
                    edtCountry?.text = item.name
                }
            }
            /*context?.showListDialog(R.string.select_country, R.array.countries) { country ->
                getViewModel().kycData.value?.birthCountry = country
            }*/
        }
        /*edtIssue?.setOnClickListener {
            context?.showListDialog(R.string.select_country, R.array.countries) { country ->
                getViewModel().kycData.value?.countryOfIssue1 = country
            }
        }
        edtIssue1?.setOnClickListener {
            context?.showListDialog(R.string.select_country, R.array.countries) { country ->
                getViewModel().kycData.value?.countryOfIssue2 = country
            }
        }
        edtIssue2?.setOnClickListener {
            context?.showListDialog(R.string.select_country, R.array.countries) { country ->
                getViewModel().kycData.value?.countryOfIssue3 = country
            }
        }*/
        btnNext?.setOnClickListener {
            getViewModel().kycData.value?.let { kycData ->
                if (isValid(kycData)) {
                    kycData.pageNo = 3
                    saveKYCData(kycData).observe(this, android.arch.lifecycle.Observer {
                        startFragment(BankAccountsFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_COMLETE_REGISTRATION, true) }), R.id.frmContainer)
                        post(kycData)
                    })
                }
            }
        }
    }

    /*private fun showCountry(item: ObservableField<String>) {
        context?.showListDialog(R.string.select_country, R.array.countries) { country ->
            item.set(country)
        }
    }*/

    private fun isValid(kycData: KYCData): Boolean {
        return when {
            kycData.birthCountry.isEmpty() -> {
                context?.simpleAlert("Please select country of birth")
                false
            }
            kycData.birthPlace.isEmpty() -> {
                context?.simpleAlert("Please enter place of birth")
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
            switchOnOff?.isChecked == false -> {
                context?.simpleAlert("We allowing only resident of India")
                false
            }
            /*!switchOnOff.isChecked && (kycData.tinNumber1.isEmpty() || kycData.countryOfIssue1.isEmpty()) -> {
                when {
                    kycData.tinNumber1.isEmpty() -> {
                        context?.simpleAlert("Please enter TIN number") {
                            edtTIN?.requestFocus()
                        }
                        false
                    }
                    kycData.countryOfIssue1.isEmpty() -> {
                        context?.simpleAlert("Please select country of issue")
                        false
                    }
                    else -> true
                }
            }
            !switchOnOff.isChecked && (!kycData.tinNumber1.isEmpty() || !kycData.countryOfIssue1.isEmpty()) -> {
                when {
                    kycData.tinNumber1.isEmpty() -> {
                        context?.simpleAlert("Please enter TIN number") {
                            edtTIN?.requestFocus()
                        }
                        false
                    }
                    kycData.countryOfIssue1.isEmpty() -> {
                        context?.simpleAlert("Please select country of issue")
                        false
                    }
                    else -> true
                }
            }*/
            else -> true
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData.value == null) {
            getViewModel().kycData.value = kycData
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
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = KYCRegistrationBFragment().apply { arguments = basket }
    }
}
