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
import com.tarrakki.module.ekyc.EKYCFragment
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.checkKYCStatus
import com.tarrakki.module.ekyc.isPANCard
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.investmentstrategies.SelectInvestmentStrategyFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

const val CATEGORYNAME = "category_name"
const val ISSINGLEINVESTMENT = "category_single_investment"
const val ISTHEMATICINVESTMENT = "category_thematic_investment"


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
                    binder.setVariable(BR.isHome, true)
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
                                    startFragment(InvestmentStrategiesFragment.newInstance(bundle), R.id.frmContainer)
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
               /* if (!item.isGoal) {
                    if (item.isThematic) {
                        val bundle = Bundle().apply {
                            putString(CATEGORYNAME, item.categoryName)
                        }
                        startFragment(InvestmentStrategiesFragment.newInstance(bundle), R.id.frmContainer)
                        postSticky(item)
                    } else {
                        val thirdLevelCategory = item.thirdLevelCategory
                        if (thirdLevelCategory.isNotEmpty()) {
                            if (thirdLevelCategory[0].categoryName.isNullOrEmpty()) {
                                if (!item.categoryDesctiption.isNullOrEmpty()) {
                                    val bundle = Bundle().apply {
                                        putString(CATEGORYNAME, item.sectionName)
                                        putBoolean(ISSINGLEINVESTMENT, true)
                                    }
                                    startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                                    postSticky(item)
                                } else {
                                    context?.investmentStragiesDialog(item.thirdLevelCategory[0]) { thirdLevelCategoryItem, amountLumpsum, amountSIP ->
                                        investmentRecommendation(thirdLevelCategoryItem.id, amountSIP, amountLumpsum, 0).observe(this,
                                                android.arch.lifecycle.Observer { response ->
                                                    val bundle = Bundle().apply {
                                                        putInt("sip", amountSIP)
                                                        putInt("lumpsump", amountLumpsum)
                                                        putInt("isFrom", 2)
                                                    }
                                                    startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                                    EventBus.getDefault().postSticky(item)
                                                    EventBus.getDefault().postSticky(item.thirdLevelCategory[0])
                                                    EventBus.getDefault().postSticky(response?.data)
                                                })
                                    }
                                }
                            } else {
                                val bundle = Bundle().apply {
                                    putString(CATEGORYNAME, item.sectionName)
                                    putBoolean(ISSINGLEINVESTMENT, false)
                                }
                                startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                                postSticky(item)
                            }
                        } else {

                            context?.simpleAlert(getString(R.string.alert_third_level_category))
                        }
                    }
                } else {
                    startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.redirectTo}") }), R.id.frmContainer)
                }*/

                activity?.onInvestmentStrategies(item)
            }
        })
        edtPanNo?.applyPAN()
        btnCheck?.setOnClickListener {
            if (edtPanNo.length() == 0) {
                context?.simpleAlert("Please enter PAN card number")
            } else if (!isPANCard(edtPanNo.text.toString())) {
                context?.simpleAlert("Please enter valid PAN card number")
            } else {
                it.dismissKeyboard()
                val kyc = KYCData(edtPanNo.text.toString(), "${App.INSTANCE.getEmail()}", "${App.INSTANCE.getMobile()}")
                checkKYCStatus(kyc).observe(this, Observer {
                    it?.let { html ->
                        //<input type='hidden' name='result' value='N|AJNPV8599B|KS101|The KYC for this PAN is not complete' />
                        try {
                            val doc = Jsoup.parse(html)
                            val values = doc.select("input[name=result]").attr("value").split("|")
                            if (values.isNotEmpty() && values.contains("N") && values.contains("KS101")) {
                                startFragment(EKYCFragment.newInstance(), R.id.frmContainer)
                                postSticky(kyc)
                            } else {
                                post(ShowError(values[3]))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        edtPanNo?.text?.clear()
                    }
                })
            }
        }

        tvWhyTarrakkii?.setOnClickListener {
            getViewModel().whayTarrakki.get()?.let {
                getViewModel().whayTarrakki.set(!it)
            }
        }

        btnIdle?.setOnClickListener {
            context?.simpleAlert(getString(R.string.coming_soon))
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
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = HomeFragment().apply { arguments = basket }
    }
}
