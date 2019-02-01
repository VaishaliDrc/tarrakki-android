package com.tarrakki.module.exploreallinvestfunds

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.tarrakki.*

import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentExploreAllInvestFundsBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.home.*
import com.tarrakki.module.invest.InvestFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.investmentstrategies.SelectInvestmentStrategyFragment
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import kotlinx.android.synthetic.main.fragment_explore_all_invest_funds.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

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

    override fun createReference() {
        setHasOptionsMenu(true)

        btnExploreFunds?.setOnClickListener {
            startFragment(InvestFragment.newInstance(), R.id.frmContainer)
        }

        rvMutualFunds.isFocusable = false
        rvMutualFunds.isNestedScrollingEnabled = false
        val observerHomeData = Observer<HomeData> {
            it?.let { apiResponse ->
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

        App.INSTANCE.widgetsViewModelB.observe(this, Observer { item ->
            if (item is HomeData.Data.Goal) {
                startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.id}") }), R.id.frmContainer)
            } else if (item is HomeData.Data.Category.SecondLevelCategory) {
                if (!item.isGoal) {
                    if (item.isThematic){
                        val bundle = Bundle().apply {
                            putString(CATEGORYNAME, item.categoryName)
                        }
                        startFragment(InvestmentStrategiesFragment.newInstance(bundle), R.id.frmContainer)
                        postSticky(item)
                    }else{
                        val thirdLevelCategory = item.thirdLevelCategory
                        if (thirdLevelCategory.isNotEmpty()) {
                            if (thirdLevelCategory[0].categoryName.isNullOrEmpty()) {
                                if (!item.categoryDesctiption.isNullOrEmpty()){
                                    val bundle = Bundle().apply {
                                        putString(CATEGORYNAME, item.sectionName)
                                        putBoolean(ISSINGLEINVESTMENT,true)
                                    }
                                    startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                                    postSticky(item)
                                }else{
                                    context?.investmentStragiesDialog(item.thirdLevelCategory[0]) { thirdLevelCategoryItem, amountLumpsum, amountSIP ->
                                        investmentRecommendation(thirdLevelCategoryItem.id, amountSIP, amountLumpsum, 0).observe(this,
                                                android.arch.lifecycle.Observer { response ->
                                                    val bundle = Bundle().apply {
                                                        putString("categoryName", item.categoryName)
                                                        putString("categoryImage", item.categoryImage)
                                                        putString("categoryDes", item.categoryDesctiption)
                                                        putString("categoryshortDes", item.categoryshortDesctiption)
                                                        putString("returnLevel", item.returnType)
                                                        putString("riskLevel", item.riskType)
                                                        putInt("sip", amountSIP)
                                                        putInt("lumpsump", amountLumpsum)
                                                        putInt("isFrom", 2)
                                                    }
                                                    startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                                    EventBus.getDefault().postSticky(item.thirdLevelCategory[0])
                                                    EventBus.getDefault().postSticky(response?.data)
                                                })
                                    }
                                }
                            } else {
                                val bundle = Bundle().apply {
                                    putString(CATEGORYNAME, item.sectionName)
                                    putBoolean(ISSINGLEINVESTMENT,false)
                                }
                                startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                                postSticky(item)
                            }
                        }else{
                            context?.simpleAlert(getString(R.string.alert_third_level_category))
                        }
                    }
                } else {
                    startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.redirectTo}") }), R.id.frmContainer)
                }
            }
        })

        mRefresh?.setOnRefreshListener {
            getViewModel().getHomeData(true).observe(this, observerHomeData)
        }

        getViewModel().getHomeData().observe(this, observerHomeData)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            tvCartCount?.text = it.toString()
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ExploreAllInvestFundsFragment().apply { arguments = basket }
    }
}
