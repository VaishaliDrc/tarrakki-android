package com.tarrakki.module.bankmandate


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentBankMandateSuccessBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_success.*
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [BankMandateSuccessFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankMandateSuccessFragment : CoreFragment<BankMandateSuccessVM, FragmentBankMandateSuccessBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate_success
    }

    override fun createViewModel(): Class<out BankMandateSuccessVM> {
        return BankMandateSuccessVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateSuccessBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnInvest?.setOnClickListener { }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment BankMandateSuccessFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateSuccessFragment().apply { arguments = basket }
    }
}
