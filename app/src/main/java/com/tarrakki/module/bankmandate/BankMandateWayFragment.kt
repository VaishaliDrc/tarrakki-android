package com.tarrakki.module.bankmandate


import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.databinding.FragmentBankMandateWayBinding
import kotlinx.android.synthetic.main.fragment_bank_mandate_way.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [BankMandateWayFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BankMandateWayFragment : CoreFragment<BankMandateWayVM, FragmentBankMandateWayBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate_way
    }

    override fun createViewModel(): Class<out BankMandateWayVM> {
        return BankMandateWayVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateWayBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        var selectedAt = 0
        rvBankMandateWay?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandateWays) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.setVariable(BR.onAdd, View.OnClickListener {
                startFragment(BankMandateFormFragment.newInstance(), R.id.frmContainer)
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
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateWayFragment().apply { arguments = basket }
    }
}
