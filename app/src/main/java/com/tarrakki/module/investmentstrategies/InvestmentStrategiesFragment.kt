package com.tarrakki.module.investmentstrategies


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentInvestmentStrategiesBinding
import com.tarrakki.databinding.RowInvestmentStrategiesItemBinding
import kotlinx.android.synthetic.main.fragment_investment_strategies.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment
import org.supportcompact.widgets.ItemOffsetDecoration

/**
 * A simple [Fragment] subclass.
 * Use the [InvestmentStrategiesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvestmentStrategiesFragment : CoreFragment<InvestmentStrategiesVM, FragmentInvestmentStrategiesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.investment_strategies)

    override fun getLayout(): Int {
        return R.layout.fragment_investment_strategies
    }

    override fun createViewModel(): Class<out InvestmentStrategiesVM> {
        return InvestmentStrategiesVM::class.java
    }

    override fun setVM(binding: FragmentInvestmentStrategiesBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvInvestmentStrategies.addItemDecoration(ItemOffsetDecoration(rvInvestmentStrategies.context, R.dimen.space_4))
        /*rvInvestmentStrategies?.setUpRecyclerView(R.layout.row_investment_strategies_item, getViewModel().investmentStrategies) { item: InvestmentStrategy, binder: RowInvestmentStrategiesItemBinding, position: Int ->
            binder.widget = item
            binder.root.setOnClickListener {
                startFragment(SelectInvestmentStrategyFragment.newInstance(), R.id.frmContainer)
            }
            binder.executePendingBindings()
        }*/
    }

    @Subscribe(sticky = true)
    fun onReceive(category: HomeData.Data.Category) {
        removeStickyEvent(category)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment SelectInvestmentStrategyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InvestmentStrategiesFragment().apply { arguments = basket }
    }
}
