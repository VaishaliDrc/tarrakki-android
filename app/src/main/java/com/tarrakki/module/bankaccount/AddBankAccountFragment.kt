package com.tarrakki.module.bankaccount


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.google.gson.Gson
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.databinding.FragmentAddBankAccountBinding
import com.tarrakki.module.verifybankaccount.VerifyBankAccountFragment
import kotlinx.android.synthetic.main.fragment_add_bank_account.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.accountTypes
import org.supportcompact.ktx.showListDialog
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AddBankAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddBankAccountFragment : CoreFragment<AddBankAccountVM, FragmentAddBankAccountBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_accounts)

    override fun getLayout(): Int {
        return R.layout.fragment_add_bank_account
    }

    override fun createViewModel(): Class<out AddBankAccountVM> {
        return AddBankAccountVM::class.java
    }

    override fun setVM(binding: FragmentAddBankAccountBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        arguments?.let {
            coreActivityVM?.title?.set(if (it.getBoolean(IS_FROM_BANK_ACCOUNT, true)) {
                getString(R.string.bank_accounts)
            } else {
                getString(R.string.bank_accounts)
            })
        }
        getViewModel().getAllBanks().observe(this, Observer { r ->
            r?.let { bankResponse ->
                bankResponse.banks?.let { banks ->
                    edtName?.setOnClickListener {
                        context?.showListDialog("Select Bank", banks) { item ->
                            getViewModel().name.set(item)
                        }
                    }
                }
            }
        })

        btnAdd?.setOnClickListener {

            if (TextUtils.isEmpty(getViewModel().name.get())) {
                context?.simpleAlert(getString(R.string.alert_req_bank_name))
            } else if (TextUtils.isEmpty(getViewModel().accountNo.get())) {
                context?.simpleAlert(getString(R.string.alert_req_bank_account_number))
            } else if (TextUtils.isEmpty(getViewModel().reenterAccountNo.get())) {
                context?.simpleAlert(getString(R.string.alert_req_confirm_bank_account_number))
            } else if (getViewModel().accountNo.get() != getViewModel().reenterAccountNo.get()) {
                context?.simpleAlert(getString(R.string.alert_match_bank_account_numbers))
            } else if (TextUtils.isEmpty(getViewModel().accountType.get())) {
                context?.simpleAlert(getString(R.string.alert_req_bank_account_type))
            } else if (TextUtils.isEmpty(getViewModel().IFSCCode.get())) {
                context?.simpleAlert(getString(R.string.alert_req_ifsc_code))
            } else if (!isIFSCCode("${getViewModel().IFSCCode.get()}")) {
                context?.simpleAlert(getString(R.string.alert_valid_bank_ifsc_code))
            } else {
                val bankId = getViewModel().response.value?.bankId(getViewModel().name.get())
                bankId?.let {
                    getViewModel().addBankDetails(it).observe(this, Observer {
                        if (it?.data?.bankDetail?.status?.equals("UPLOADED", true)!!) {
                            val bundle = Bundle()
                            bundle.putString("userBankData", Gson().toJson(it))
                            startFragment(VerifyBankAccountFragment.newInstance(bundle), R.id.frmContainer)
                        } else {
                            context?.simpleAlert(getString(R.string.alert_success_new_bank)) {
                                onBack()
                                coreActivityVM?.onNewBank?.value = true
                            }
                        }


                    })
                }
            }


        }
        tvAccountType?.setOnClickListener {
            context?.accountTypes { item ->
                getViewModel().accountType.set(item)
            }
        }

    }

    private fun isIFSCCode(IFSCCode: String) = IFSCCode.length == 11//Pattern.compile("[A-Z|a-z]{4}[0][\\d]{6}\$").matcher(IFSCCode).matches()

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AddBankAccountFragment().apply { arguments = basket }
    }
}
