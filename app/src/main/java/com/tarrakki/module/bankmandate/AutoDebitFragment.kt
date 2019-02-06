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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.applyCurrencyFormatPositiveOnly
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [AutoDebitFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
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
        var selectedAt = 1
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
        btnContinue?.setOnClickListener {
            val bundle = Bundle().apply {
                putString("amount",getViewModel().amount.get())
            }
            startFragment(BankMandateWayFragment.newInstance(bundle), R.id.frmContainer)
        }
        edtInvestAmount?.applyCurrencyFormatPositiveOnly()
        getViewModel().amount.set(getViewModel().ammounts[2].amount.toString())

    }

    /*override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
*/
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().bankMandate.set(data)
        //EventBus.getDefault().removeStickyEvent(data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment AutoDebitFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AutoDebitFragment().apply { arguments = basket }
    }
}
