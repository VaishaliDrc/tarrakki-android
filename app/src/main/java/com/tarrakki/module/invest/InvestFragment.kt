package com.tarrakki.module.invest


import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ArrayAdapter
import com.tarrakki.R
import com.tarrakki.databinding.*
import kotlinx.android.synthetic.main.fragment_invest.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView


/**
 * A simple [Fragment] subclass.
 * Use the [InvestFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class InvestFragment : CoreFragment<InvestVM, FragmentInvestBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.invest)

    override fun getLayout(): Int {
        return R.layout.fragment_invest
    }

    override fun createViewModel(): Class<out InvestVM> {
        return InvestVM::class.java
    }

    override fun setVM(binding: FragmentInvestBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        ivExClp?.setOnClickListener { _ ->
            getViewModel().filter.set(!getViewModel().filter.get()!!)
        }
        var riskLevel = 0
        rvRiskLevel?.setUpRecyclerView(R.layout.row_risk_level_indicator, getViewModel().arrRiskLevel) { item: RiskLevel, binder: RowRiskLevelIndicatorBinding, position ->
            binder.riskLevel = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                getViewModel().arrRiskLevel[riskLevel].isSelected = false
                item.isSelected = !item.isSelected
                riskLevel = position
            }
        }
        rvFundType?.setUpRecyclerView(R.layout.row_fund_type_list_item, getViewModel().fundTypes) { item: FundType, binder: RowFundTypeListItemBinding, position ->
            binder.fundType = item
            binder.executePendingBindings()
            binder.tvFundType.setOnClickListener {
                item.isSelected = !item.isSelected
            }
        }
        var selectedAt = 1
        rvFundReturns?.setUpRecyclerView(R.layout.row_fund_returns_list_item, getViewModel().fundReturns) { item: FundType, binder: RowFundReturnsListItemBinding, position ->
            binder.fundType = item
            binder.executePendingBindings()
            binder.tvFundType.setOnClickListener {
                if (selectedAt != -1) {
                    getViewModel().fundReturns[selectedAt].isSelected = false
                }
                item.isSelected = !item.isSelected
                selectedAt = position
            }
        }
        rvFunds?.isFocusable = false
        rvFunds?.isNestedScrollingEnabled = false
        rvFunds?.setUpRecyclerView(R.layout.row_fund_list_item, getViewModel().funds) { item: Fund, binder: RowFundListItemBinding, position ->
            binder.fund = item
            binder.executePendingBindings()
        }

        val adapter = ArrayAdapter.createFromResource(
                activity,
                R.array.risk_level,
                R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spnCategory.adapter = adapter
        spnSubCategory.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.119400041473
         * @return A new instance of fragment InvestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InvestFragment().apply { arguments = basket }
    }
}
