package com.tarrakki.module.portfolio.fragments

import android.os.Bundle
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tarrakki.R
import com.tarrakki.api.model.*
import com.tarrakki.databinding.FragmentLiquiloanPortfolioBinding
import com.tarrakki.databinding.RowGoalBasedInvestmentListItemBinding
import com.tarrakki.databinding.RowLiquiloansPortfolioBinding
import com.tarrakki.module.portfolio.PortfolioDetailsFragment
import com.tarrakki.module.portfolio.PortfolioVM
import com.tarrakki.showComingSoonAddRedeemPortfolio
import kotlinx.android.synthetic.main.fragment_consumer_loan_liqui.mRefresh
import kotlinx.android.synthetic.main.fragment_goal_based_investment.*
import kotlinx.android.synthetic.main.fragment_liquiloan_portfolio.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.BaseAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.ArrayList

class LiquiLoanPortfolioFragment : CoreFragment<PortfolioVM, FragmentLiquiloanPortfolioBinding>() {

    var vm: PortfolioVM? = null
    var adapter : BaseAdapter<GetLiquiLoanPortFolioData, RowLiquiloansPortfolioBinding>? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_liquiloan_portfolio
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentLiquiloanPortfolioBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        rvLiquilonsPortfolio?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        parentFragment?.let {
            vm = ViewModelProviders.of(it).get(PortfolioVM::class.java)
        }

        mRefresh?.setOnRefreshListener {
            vm?.getLiquiloansPortfolioAPI()
        }

        vm?.isRefreshing?.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
            }
        })

        vm?.let { vm ->
            vm.userPortfolioData.observe(this, Observer {
                if (it?.data?.isNotEmpty() == true) {
                    getViewModel().isPortfolioEmpty.set(false)
                    adapter  = rvLiquilonsPortfolio?.setUpRecyclerView(R.layout.row_liquiloans_portfolio,
                            it.data as ArrayList<GetLiquiLoanPortFolioData>) { item: GetLiquiLoanPortFolioData, binder: RowLiquiloansPortfolioBinding, position ->
                        binder.vm = item
                        binder.executePendingBindings()
                        binder.setVariable(BR.onAddClick, View.OnClickListener {
                            context?.showComingSoonAddRedeemPortfolio()
                        })

                        binder.setVariable(BR.onRedeemClick, View.OnClickListener {
                            context?.showComingSoonAddRedeemPortfolio()
                        })

                        binder.setVariable(BR.onHideDetailsClick, View.OnClickListener {
                            item.hideDetails = !item.hideDetails
                            adapter?.notifyItemChanged(position)
                        })

                    }
                } else {
                    getViewModel().isPortfolioEmpty.set(true)
                    //coreActivityVM?.emptyView(true)
                }
            })
        }



/*
        vm?.getLiquiloansPortfolioAPI()
*/

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = LiquiLoanPortfolioFragment().apply { arguments = basket }
    }
}
