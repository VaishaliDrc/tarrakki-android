package com.tarrakki.module.investmentstrategies


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentSelectInvestmentStrategiesBinding
import com.tarrakki.databinding.PageInvestmentOptionsItemBinding
import kotlinx.android.synthetic.main.fragment_select_investment_strategies.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setPageAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [SelectInvestmentStrategyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectInvestmentStrategyFragment : CoreFragment<SelectInvestmentStrategyVM, FragmentSelectInvestmentStrategiesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.investment_strategies)

    override fun getLayout(): Int {
        return R.layout.fragment_select_investment_strategies
    }

    override fun createViewModel(): Class<out SelectInvestmentStrategyVM> {
        return SelectInvestmentStrategyVM::class.java
    }

    override fun setVM(binding: FragmentSelectInvestmentStrategiesBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        tvNoteToInvestorsi?.setOnClickListener {
            getViewModel().noteToInvestors.get()?.let { isOpen ->
                getViewModel().noteToInvestors.set(!isOpen)
            }
        }
        mPager?.setPagingEnabled(true)
        mPager?.setPageAdapter(R.layout.page_investment_options_item, getViewModel().investmentOptions) { binder: PageInvestmentOptionsItemBinding, item: InvestmentOption ->
            binder.invest = item
            binder.ivNext.setOnClickListener {
                mPager.currentItem = mPager.currentItem + 1
            }
            binder.ivPrevious.setOnClickListener {
                mPager.currentItem = mPager.currentItem - 1
            }
            binder.executePendingBindings()
        }
        pageIndicator?.setViewPager(mPager)
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
        fun newInstance(basket: Bundle? = null) = SelectInvestmentStrategyFragment().apply { arguments = basket }
    }
}
