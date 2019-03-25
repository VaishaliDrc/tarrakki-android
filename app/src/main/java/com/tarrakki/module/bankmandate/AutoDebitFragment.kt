package com.tarrakki.module.bankmandate


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.BankDetail
import com.tarrakki.databinding.FragmentAutoDebitBinding
import com.tarrakki.databinding.RowAutoDebitAmountListItemBinding
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.fragment_auto_debit.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import java.math.BigInteger

const val AMOUNT = "amount"

class AutoDebitFragment : CoreFragment<AutoMandateVM, FragmentAutoDebitBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_auto_debit
    }

    override fun createViewModel(): Class<out AutoMandateVM> {
        return AutoMandateVM::class.java
    }

    override fun setVM(binding: FragmentAutoDebitBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        val flowLayoutManager = FlowLayoutManager()
        flowLayoutManager.isAutoMeasureEnabled = true
        flowLayoutManager.setAlignment(Alignment.CENTER)
        flowLayoutManager.maxItemsPerLine(3)
        rvAmounts?.layoutManager = flowLayoutManager
        var selectedAt = 3
        rvAmounts?.setUpRecyclerView(R.layout.row_auto_debit_amount_list_item, getViewModel().ammounts) { item: AutoDebitAmount, binder: RowAutoDebitAmountListItemBinding, position ->
            binder.fundType = item
            binder.tvFundType.setOnClickListener {
                if (selectedAt != -1) {
                    getViewModel().ammounts[selectedAt].isSelected = false
                }
                item.isSelected = !item.isSelected
                selectedAt = position
                getViewModel().amount.set(item.amount.toString())
            }
            binder.executePendingBindings()
        }
        val amountSelected = getViewModel().ammounts.find { it.isSelected }
        if (amountSelected != null)
            getViewModel().amount.set(amountSelected.amount.toString())

        btnContinue?.setOnClickListener {
            val amount = getViewModel().amount.get()?.toCurrencyBigInt()
            if (amount != BigInteger.ZERO) {
                if (amount != null) {
                    if (amount>=BigInteger.valueOf(10000)){
                        val bundle = Bundle().apply {
                            putString(AMOUNT, amount.toString())
                        }
                        startFragment(BankMandateWayFragment.newInstance(bundle), R.id.frmContainer)
                    }else{
                        context?.simpleAlert("The amount must be greater than or equal to "+10000.0.toCurrency())
                    }
                }
            }else{
                context?.simpleAlert(getString(R.string.alert_select_amount))
            }
        }
        edtInvestAmount?.applyCurrencyFormatPositiveOnly()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().bankMandate.set(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AutoDebitFragment().apply { arguments = basket }
    }
}
