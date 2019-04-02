package com.tarrakki.module.exploreallinvestfunds

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentExploreAllInvestFundsBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.home.HomeSection
import com.tarrakki.module.invest.InvestFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import com.tarrakki.onInvestmentStrategies
import kotlinx.android.synthetic.main.fragment_explore_all_invest_funds.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.cartCount
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import org.supportcompact.utilise.EqualSpacingItemDecoration

class ExploreAllInvestFundsFragment : CoreFragment<ExploreAllInvestmentFundsVM, FragmentExploreAllInvestFundsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.mutual_funds)

    override fun getLayout(): Int {
        return R.layout.fragment_explore_all_invest_funds
    }

    override fun createViewModel(): Class<out ExploreAllInvestmentFundsVM> {
        return ExploreAllInvestmentFundsVM::class.java
    }

    override fun setVM(binding: FragmentExploreAllInvestFundsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    val observerHomeData = Observer<HomeData> {
        it?.let { apiResponse ->
            btnIdle?.visibility = View.VISIBLE
            btnExploreFunds?.visibility = View.VISIBLE
            mRefresh?.isRefreshing = false
            rvMutualFunds.setUpMultiViewRecyclerAdapter(getViewModel().homeSections) { item, binder, position ->
                binder.setVariable(BR.section, item)
                binder.setVariable(BR.onViewAll, View.OnClickListener {
                    if (item is HomeSection)
                        when ("${item.title}") {
                            "Set a Goal" -> {
                                startFragment(GoalFragment.newInstance(), R.id.frmContainer)
                            }
                            else -> {
                                val bundle = Bundle().apply {
                                    putString(CATEGORYNAME, item.title)
                                }
                                startFragment(InvestmentStrategiesFragment.newInstance(bundle)
                                        , R.id.frmContainer)
                                item.category?.let { postSticky(it) }
                            }
                        }
                })
                binder.executePendingBindings()
            }
            rvMutualFunds.visibility = View.VISIBLE
        }
    }

    @Subscribe(sticky = true)
    fun onEventData(event: Event) {
        when (event) {
            Event.ISGOALADDED -> {
                getViewModel().getHomeData().observe(this, observerHomeData)
            }
        }
    }

    override fun createReference() {
        rvMutualFunds?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        setHasOptionsMenu(true)

        btnIdle?.setOnClickListener {
            context?.simpleAlert(getString(R.string.coming_soon))
        }

        btnExploreFunds?.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isEnableBack", true)
            }
            startFragment(InvestFragment.newInstance(bundle), R.id.frmContainer)
        }

        rvMutualFunds.isFocusable = false
        rvMutualFunds.isNestedScrollingEnabled = false
        App.INSTANCE.widgetsViewModelB.value = null
        App.INSTANCE.widgetsViewModelB.observe(this, Observer { item ->
            if (item is HomeData.Data.Goal) {
                startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.id}") }), R.id.frmContainer)
            } else if (item is HomeData.Data.Category.SecondLevelCategory) {
                activity?.onInvestmentStrategies(item)
            }
        })

        mRefresh?.setOnRefreshListener {
            getViewModel().getHomeData(true).observe(this, observerHomeData)
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })

        getViewModel().getHomeData().observe(this, observerHomeData)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            it?.let {
                tvCartCount?.cartCount(it)
            }
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
    }*/

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ExploreAllInvestFundsFragment().apply { arguments = basket }
    }
}
