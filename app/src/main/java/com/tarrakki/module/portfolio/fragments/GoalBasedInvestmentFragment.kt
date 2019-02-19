package com.tarrakki.module.portfolio.fragments


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tarrakki.R
import com.tarrakki.databinding.FragmentGoalBasedInvestmentBindingImpl
import com.tarrakki.databinding.RowGoalBasedInvestmentListItemBinding
import com.tarrakki.module.portfolio.Investment
import com.tarrakki.module.portfolio.PortfolioDetailsFragment
import com.tarrakki.module.portfolio.PortfolioVM
import kotlinx.android.synthetic.main.fragment_goal_based_investment.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [GoalBasedInvestmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GoalBasedInvestmentFragment : Fragment() {

    var vm: PortfolioVM? = null
    var binder: FragmentGoalBasedInvestmentBindingImpl? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (binder == null) {
            binder = DataBindingUtil.inflate(inflater, R.layout.fragment_goal_based_investment, container, false)
            parentFragment?.let {
                vm = ViewModelProviders.of(it).get(PortfolioVM::class.java)
            }
        }
        return binder?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm?.let { vm ->
            rvGBInvests?.setUpRecyclerView(R.layout.row_goal_based_investment_list_item, vm.goalBasedInvestment) { item: Investment, binder: RowGoalBasedInvestmentListItemBinding, position ->
                binder.investment = item
                binder.executePendingBindings()
                binder.root.setOnClickListener {
                    startFragment(PortfolioDetailsFragment.newInstance(), R.id.frmContainer)
                    EventBus.getDefault().postSticky(item)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = GoalBasedInvestmentFragment().apply { arguments = basket }
    }
}
