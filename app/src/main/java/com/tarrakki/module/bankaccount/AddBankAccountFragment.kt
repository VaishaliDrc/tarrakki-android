package com.tarrakki.module.bankaccount


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.widget.ArrayAdapter
import com.tarrakki.IS_FROM_BANK_ACCOUNT
import com.tarrakki.R
import com.tarrakki.databinding.FragmentAddBankAccountBinding
import kotlinx.android.synthetic.main.fragment_add_bank_account.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.accountTypes
import org.supportcompact.ktx.simpleAlert

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
        context?.let {
            // Get the string array
            val countries: Array<out String> = it.resources.getStringArray(R.array.list_of_bank)
            // Create the adapter and set it to the AutoCompleteTextView
            ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, countries).also { adapter ->
                edtName.setAdapter(adapter)
            }
        }
        btnAdd?.setOnClickListener {
            if (TextUtils.isEmpty(getViewModel().name.get())) {
                context?.simpleAlert("Please enter bank name")
            } else if (TextUtils.isEmpty(getViewModel().accountNo.get())) {
                context?.simpleAlert("Please enter account number")
            } else if (TextUtils.isEmpty(getViewModel().reenterAccountNo.get())) {
                context?.simpleAlert("Please enter re-enter account number")
            } else if (getViewModel().accountNo.get() == getViewModel().reenterAccountNo.get()) {
                context?.simpleAlert("Account number and re-enter account number should be same")
            } else if (TextUtils.isEmpty(getViewModel().breachName.get())) {
                context?.simpleAlert("Please enter breach name")
            } else if (TextUtils.isEmpty(getViewModel().accountType.get())) {
                context?.simpleAlert("Please select account type")
            } else if (TextUtils.isEmpty(getViewModel().IFSCCode.get())) {
                context?.simpleAlert("Please enter IFSC code")
            } else {
                context?.simpleAlert("New Bank Account has been added successfully") {
                    activity?.onBackPressed()
                }
            }
        }
        tvAccountType?.setOnClickListener {
            context?.accountTypes { item ->
                getViewModel().accountType.set(item)
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment AddBankAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AddBankAccountFragment().apply { arguments = basket }
    }
}
