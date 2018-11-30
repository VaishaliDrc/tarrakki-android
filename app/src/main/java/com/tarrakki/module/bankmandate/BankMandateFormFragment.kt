package com.tarrakki.module.bankmandate


import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentBankMandateFormBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_form.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [BankMandateFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankMandateFormFragment : CoreFragment<BankMandateFormVM, FragmentBankMandateFormBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate_form
    }

    override fun createViewModel(): Class<out BankMandateFormVM> {
        return BankMandateFormVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateFormBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        var selectedAt = 0
        rvBankMandateForm?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandateWays) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.setVariable(BR.onAdd, View.OnClickListener {
                startFragment(BankMandateSuccessFragment.newInstance(), R.id.frmContainer)
            })
            binder.root.setOnClickListener {
                if (item is BankMandateWay) {
                    val data = getViewModel().bankMandateWays[selectedAt]
                    if (data is BankMandateWay) {
                        data.isSelected = false
                    }
                    item.isSelected = true
                    selectedAt = position
                }
            }
            binder.executePendingBindings()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment BankMandateFormFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFormFragment().apply { arguments = basket }
    }
}
