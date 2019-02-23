package com.tarrakki.module.portfolio.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.databinding.FragmentGoalBasedInvestmentBinding
import com.tarrakki.databinding.RowGoalBasedInvestmentListItemBinding
import com.tarrakki.module.portfolio.PortfolioDetailsFragment
import com.tarrakki.module.portfolio.PortfolioVM
import kotlinx.android.synthetic.main.fragment_goal_based_investment.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment
import org.supportcompact.utilise.EqualSpacingItemDecoration
import java.util.*

class GoalBasedInvestmentFragment : CoreFragment<PortfolioVM, FragmentGoalBasedInvestmentBinding>() {

    var vm: PortfolioVM? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_goal_based_investment
    }

    override fun createViewModel(): Class<out PortfolioVM> {
        return PortfolioVM::class.java
    }

    override fun setVM(binding: FragmentGoalBasedInvestmentBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvGBInvests?.addItemDecoration(EqualSpacingItemDecoration(44))

        parentFragment?.let {
            vm = ViewModelProviders.of(it).get(PortfolioVM::class.java)
        }

        vm?.isRefreshing?.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
            }
        })

        vm?.let { vm ->
            vm.portfolioData.observe(this, Observer {
                if (it?.data?.goalBasedInvestment?.isNotEmpty() == true) {
                    getViewModel().isGoalEmpty.set(false)
                    rvGBInvests?.setUpRecyclerView(R.layout.row_goal_based_investment_list_item,
                            it.data.goalBasedInvestment as ArrayList<UserPortfolioResponse.Data.GoalBasedInvestment>) { item: UserPortfolioResponse.Data.GoalBasedInvestment, binder: RowGoalBasedInvestmentListItemBinding, position ->
                        binder.investment = item
                        binder.executePendingBindings()
                        binder.root.setOnClickListener {
                            startFragment(PortfolioDetailsFragment.newInstance(), R.id.frmContainer)
                            EventBus.getDefault().postSticky(item)
                        }
                    }
                } else {
                    getViewModel().isGoalEmpty.set(true)
                    //coreActivityVM?.emptyView(true)
                }
            })
        }

        mRefresh?.setOnRefreshListener {
            vm?.getUserPortfolio(true)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = GoalBasedInvestmentFragment().apply { arguments = basket }
    }
}
