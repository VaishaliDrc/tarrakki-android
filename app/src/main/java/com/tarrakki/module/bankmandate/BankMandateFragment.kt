package com.tarrakki.module.bankmandate


import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentBankMandateBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [BankMandateFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate
    }

    override fun createViewModel(): Class<out BankMandateVM> {
        return BankMandateVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvBankMandate?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandate) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.executePendingBindings()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment BankMandateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFragment().apply { arguments = basket }
    }
}
