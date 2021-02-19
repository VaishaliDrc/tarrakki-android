package com.tarrakki.module.investmentstrategies


import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentInvestmentStrategiesBinding
import com.tarrakki.databinding.RowInvestmentStrategiesItemBinding
import com.tarrakki.databinding.RowThirdLevelInvestmentBinding
import com.tarrakki.investmentRecommendation
import com.tarrakki.investmentStragiesDialog
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.home.ISSINGLEINVESTMENT
import com.tarrakki.module.home.ISTHEMATICINVESTMENT
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import com.tarrakki.onInvestmentStrategies
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
                    activity?.onInvestmentStrategies(this,true,item)
                }
                binder.executePendingBindings()
            }
        })

        getViewModel().secondaryCategories.observe(this, Observer {
            rvInvestmentStrategies?.setUpRecyclerView(R.layout.row_third_level_investment,
                    it?.thirdLevelCategory as ArrayList<HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory>) { item: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory, binder: RowThirdLevelInvestmentBinding, position: Int ->
                binder.widget = item
                binder.tvDescription.visibility = View.INVISIBLE
                binder.root.setOnClickListener { view ->
                    if (!item.categoryDesctiption.isNullOrEmpty()) {
                        val bundle = Bundle().apply {
                            putString(CATEGORYNAME, title)
                            putBoolean(ISSINGLEINVESTMENT, true)
                            putBoolean(ISTHEMATICINVESTMENT, true)
                        }
                        startFragment(SelectInvestmentStrategyFragment.newInstance(bundle), R.id.frmContainer)
                        EventBus.getDefault().postSticky(it)
                        EventBus.getDefault().postSticky(item)
                    } else {
                        context?.investmentStragiesDialog(item) { thirdLevelCategory, amountLumpsum, amountSIP ->
                            investmentRecommendation(this,true,thirdLevelCategory.id, amountSIP, amountLumpsum, 0).observe(this,
                                    androidx.lifecycle.Observer { response ->
                                        val bundle = Bundle().apply {
                                            putInt("isFrom", 2)
                                            putString("sip", amountSIP.toString())
                                            putString("lumpsump", amountLumpsum.toString())
                                        }
                                        startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                        EventBus.getDefault().postSticky(it)
                                        EventBus.getDefault().postSticky(item)
                                        EventBus.getDefault().postSticky(response?.data)
                                    })
                        }
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
            //removeStickyEvent(category)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = InvestmentStrategiesFragment().apply { arguments = basket }
    }
}