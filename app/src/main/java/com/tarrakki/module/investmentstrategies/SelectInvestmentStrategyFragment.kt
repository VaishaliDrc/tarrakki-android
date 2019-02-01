package com.tarrakki.module.investmentstrategies

import android.os.Bundle
import android.view.View
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentSelectInvestmentStrategiesBinding
import com.tarrakki.databinding.PageInvestmentOptionsItemBinding
import com.tarrakki.investmentRecommendation
import com.tarrakki.investmentStragiesDialog
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.home.ISSINGLEINVESTMENT
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import kotlinx.android.synthetic.main.fragment_select_investment_strategies.*
import kotlinx.android.synthetic.main.fragment_select_investment_strategies.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setPageAdapter
import org.supportcompact.ktx.startFragment
import java.util.*

class SelectInvestmentStrategyFragment : CoreFragment<SelectInvestmentStrategyVM, FragmentSelectInvestmentStrategiesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = arguments?.getString(CATEGORYNAME).toString()

    var isSingleInvestment: Boolean? = false

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
        isSingleInvestment = arguments?.getBoolean(ISSINGLEINVESTMENT)

        btnContinue?.setOnClickListener {
            val category = getViewModel().secondlevel.get()
            category?.thirdLevelCategory?.get(0)?.let { it1 ->
                context?.investmentStragiesDialog(it1) { thirdLevelCategory, amountLumpsum, amountSIP ->
                    investmentRecommendation(thirdLevelCategory.id, amountSIP, amountLumpsum, 0).observe(this,
                            android.arch.lifecycle.Observer { response ->
                                val bundle = Bundle().apply {
                                    putString("categoryName", category.categoryName)
                                    putString("categoryImage", category.categoryImage)
                                    putString("categoryDes", category.categoryDesctiption)
                                    putString("categoryshortDes", category.categoryshortDesctiption)
                                    putString("returnLevel", category.returnType)
                                    putString("riskLevel", category.riskType)
                                    putInt("isFrom", 2)
                                    putInt("sip", amountSIP)
                                    putInt("lumpsump", amountLumpsum)
                                }
                                startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                EventBus.getDefault().postSticky(category.thirdLevelCategory?.get(0))
                                EventBus.getDefault().postSticky(response?.data)
                            })
                }
            }
        }

        tvNoteToInvestorsi?.setOnClickListener {
            getViewModel().noteToInvestors.get()?.let { isOpen ->
                getViewModel().noteToInvestors.set(!isOpen)
            }
        }
        mPager?.setPagingEnabled(true)

        if (isSingleInvestment == true) {
            tvNoteToInvestorsi?.visibility = View.GONE
            lyt_pager?.visibility = View.GONE
            lyt_single_investment?.visibility = View.VISIBLE
        } else {
            tvNoteToInvestorsi?.visibility = View.VISIBLE
            lyt_pager.visibility = View.VISIBLE
            lyt_single_investment?.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = SelectInvestmentStrategyFragment().apply { arguments = basket }
    }

    @Subscribe(sticky = true)
    fun onReceive(category: HomeData.Data.Category.SecondLevelCategory) {
        getViewModel().txtnoteToInvestors.set(category.noteToInvestor)
        getViewModel().secondlevel.set(category)
        setAdapter(category)
        removeStickyEvent(category)

        getViewModel().secondlevel.get()?.riskLevelDrawable?.let { tvRiskLevel?.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0) }
        getViewModel().secondlevel.get()?.returnRiskDrawable?.let { tvReturnLevel?.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0) }
    }

    private fun setAdapter(category: HomeData.Data.Category.SecondLevelCategory) {
        if (category.thirdLevelCategory.isNotEmpty()) {
            for (data in category.thirdLevelCategory) {
                data.hasNext = true
                data.hasPrevious = true
            }

            category.thirdLevelCategory[category.thirdLevelCategory.size - 1].hasNext = false
            category.thirdLevelCategory[0].hasPrevious = false
        }

        mPager?.setPageAdapter(R.layout.page_investment_options_item,
                category.thirdLevelCategory as ArrayList<HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory>)
        { binder: PageInvestmentOptionsItemBinding,
          item: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory ->
            binder.invest = item

            binder.tvRiskLevel.setCompoundDrawablesWithIntrinsicBounds(item.riskLevelDrawable, 0, 0, 0)
            binder.tvReturnLevel.setCompoundDrawablesWithIntrinsicBounds(item.returnRiskDrawable, 0, 0, 0)

            binder.ivNext.setOnClickListener {
                mPager.currentItem = mPager.currentItem + 1
            }
            binder.ivPrevious.setOnClickListener {
                mPager.currentItem = mPager.currentItem - 1
            }
            binder.btnSelect.setOnClickListener {
                context?.investmentStragiesDialog(item) { thirdLevelCategory, amountLumpsum, amountSIP ->
                    investmentRecommendation(thirdLevelCategory.id, amountSIP, amountLumpsum, 0).observe(this,
                            android.arch.lifecycle.Observer { response ->
                                val bundle = Bundle().apply {
                                    putString("categoryName", category.categoryName)
                                    putString("categoryImage", category.categoryImage)
                                    putString("categoryDes", item.categoryDesctiption)
                                    putString("categoryshortDes", item.shortDescroption)
                                    putString("returnLevel", item.returnType)
                                    putString("riskLevel", item.riskType)
                                    putInt("isFrom", 1)
                                    putInt("sip", amountSIP)
                                    putInt("lumpsump", amountLumpsum)
                                }
                                startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                EventBus.getDefault().postSticky(item)
                                EventBus.getDefault().postSticky(response?.data)
                            })
                }
            }
            binder.executePendingBindings()
        }
        pageIndicator?.setViewPager(mPager)
    }
}
