package com.tarrakki.module.home


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import com.tarrakki.*
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentHomeBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.investmentstrategies.SelectInvestmentStrategyFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

const val CATEGORYNAME = "category_name"

class HomeFragment : CoreFragment<HomeVM, FragmentHomeBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.home)

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun createViewModel(): Class<out HomeVM> {
        return HomeVM::class.java
    }

    override fun setVM(binding: FragmentHomeBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)
        rvHomeItem.isFocusable = false
        rvHomeItem.isNestedScrollingEnabled = false
        val observerHomeData = Observer<HomeData> {
            it?.let { apiResponse ->
                mRefresh?.isRefreshing = false
                rvHomeItem.setUpMultiViewRecyclerAdapter(getViewModel().homeSections) { item, binder, position ->
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
                rvHomeItem.visibility = View.VISIBLE
            }
        }

        App.INSTANCE.widgetsViewModel.observe(this, Observer { item ->
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
                            if (thirdLevelCategory[0].categoryName != null) {
                                val bundle = Bundle().apply {
                                    putString(CATEGORYNAME, item.sectionName)
                                }
                                startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                                postSticky(item)
                            } else {
                                context?.investmentStragiesDialog(item.thirdLevelCategory[0]) { thirdLevelCategoryItem, amountLumpsum, amountSIP ->
                                    investmentRecommendation(thirdLevelCategoryItem.id, amountSIP, amountLumpsum, 0).observe(this,
                                            android.arch.lifecycle.Observer { response ->
                                                val bundle = Bundle().apply {
                                                    putString("categoryName", item.categoryName)
                                                    putString("categoryImage", item.categoryImage)
                                                    putString("categoryDes", item.categoryDesctiption)
                                                    putInt("isFrom", 2)
                                                }
                                                startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                                EventBus.getDefault().postSticky(item.thirdLevelCategory[0])
                                                EventBus.getDefault().postSticky(response?.data)
                                            })
                                }
                            }
                        }
                    }
                } else {
                    startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.redirectTo}") }), R.id.frmContainer)
                }
                //startFragment(SelectInvestmentStrategyFragment.newInstance(), R.id.frmContainer)
            }
        })

        tvWhyTarrakkii?.setOnClickListener { _ ->
            getViewModel().whayTarrakki.get()?.let {
                getViewModel().whayTarrakki.set(!it)
            }
        }
        tvViewPortfolio?.setOnClickListener {
            startFragment(PortfolioFragment.newInstance(), R.id.frmContainer)
        }
        mRefresh?.setOnRefreshListener {
            getViewModel().getHomeData(true).observe(this, observerHomeData)
        }

        cpPortfolio.setProgressWithAnimation(78f)
        App.INSTANCE.isLoggedIn.observe(this, Observer {
            it?.let { isLogin ->
                getViewModel().portfolioVisibility.set(if (isLogin) View.VISIBLE else View.GONE)
            }
        })
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket .
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = HomeFragment().apply { arguments = basket }
    }
}
