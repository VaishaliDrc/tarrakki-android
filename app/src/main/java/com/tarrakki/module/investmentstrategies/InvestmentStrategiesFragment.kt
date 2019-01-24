package com.tarrakki.module.investmentstrategies


import android.arch.lifecycle.Observer
import android.os.Bundle
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentInvestmentStrategiesBinding
import com.tarrakki.databinding.RowInvestmentStrategiesItemBinding
import com.tarrakki.databinding.RowThirdLevelInvestmentBinding
import com.tarrakki.investmentRecommendation
import com.tarrakki.investmentRecommendationToCart
import com.tarrakki.investmentStragiesDialog
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import kotlinx.android.synthetic.main.fragment_investment_strategies.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment
import org.supportcompact.widgets.ItemOffsetDecoration
import java.util.*

class InvestmentStrategiesFragment : CoreFragment<InvestmentStrategiesVM, FragmentInvestmentStrategiesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = arguments?.getString(CATEGORYNAME).toString()

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

        getViewModel().secondaryCategoriesList.observe(this, Observer {
            rvInvestmentStrategies?.setUpRecyclerView(R.layout.row_investment_strategies_item,
                    it as ArrayList<HomeData.Data.Category.SecondLevelCategory>) { item: HomeData.Data.Category.SecondLevelCategory, binder: RowInvestmentStrategiesItemBinding, position: Int ->
                binder.widget = item
                binder.root.setOnClickListener { view ->
                    if (!item.isGoal) {
                        if (item.isThematic) {
                            val bundle = Bundle().apply {
                                putString(CATEGORYNAME, item.categoryName)
                            }
                            startFragment(InvestmentStrategiesFragment.newInstance(bundle), R.id.frmContainer)
                            postSticky(item)
                        } else {
                            val thirdLevelCategory = item.thirdLevelCategory
                            if (thirdLevelCategory.isNotEmpty()) {
                                if (thirdLevelCategory[0].categoryName != null) {
                                    val bundle = Bundle().apply {
                                        putString(CATEGORYNAME, title)
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
                }
                binder.executePendingBindings()
            }
        })

        getViewModel().secondaryCategories.observe(this, Observer {
            rvInvestmentStrategies?.setUpRecyclerView(R.layout.row_third_level_investment,
                    it?.thirdLevelCategory as ArrayList<HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory>) { item: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory, binder: RowThirdLevelInvestmentBinding, position: Int ->
                binder.widget = item
                binder.root.setOnClickListener { view ->
                    context?.investmentStragiesDialog(item) { thirdLevelCategory, amountLumpsum, amountSIP ->
                        investmentRecommendation(thirdLevelCategory.id, amountSIP, amountLumpsum, 0).observe(this,
                                android.arch.lifecycle.Observer { response ->
                                    val bundle = Bundle().apply {
                                        putString("categoryName",it.categoryName)
                                        putString("categoryImage",it.categoryImage)
                                        putString("categoryDes",it.categoryDesctiption)
                                        putInt("isFrom",2)
                                    }
                                    startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                    EventBus.getDefault().postSticky(item)
                                    EventBus.getDefault().postSticky(response?.data)
                                })
                    }
                }
                binder.executePendingBindings()
            }
        })
    }

    @Subscribe(sticky = true)
    fun onReceive(category: HomeData.Data.Category) {
        removeStickyEvent(category)
        getViewModel().secondaryCategoriesList.value = category.secondLevelCategory
    }

    @Subscribe(sticky = true)
    fun onThemeticReceive(category: HomeData.Data.Category.SecondLevelCategory) {
        if (getViewModel().secondaryCategoriesList.value == null) {
            getViewModel().secondaryCategories.value = category
            removeStickyEvent(category)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InvestmentStrategiesFragment().apply { arguments = basket }
    }
}
