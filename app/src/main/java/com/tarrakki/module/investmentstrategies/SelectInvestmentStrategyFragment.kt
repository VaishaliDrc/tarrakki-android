package com.tarrakki.module.investmentstrategies

import android.os.Bundle
import android.support.v4.view.ViewPager
import com.tarrakki.R
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentSelectInvestmentStrategiesBinding
import com.tarrakki.databinding.PageInvestmentOptionsItemBinding
import com.tarrakki.investmentRecommendation
import com.tarrakki.investmentStragiesDialog
import com.tarrakki.module.home.CATEGORYNAME
import com.tarrakki.module.home.ISSINGLEINVESTMENT
import com.tarrakki.module.home.ISTHEMATICINVESTMENT
import com.tarrakki.module.recommended.RecommendedBaseOnRiskLevelFragment
import kotlinx.android.synthetic.main.fragment_select_investment_strategies.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setPageAdapter
import org.supportcompact.adapters.setWrapContentPageAdapter
import org.supportcompact.ktx.startFragment
import java.util.*

class SelectInvestmentStrategyFragment : CoreFragment<SelectInvestmentStrategyVM, FragmentSelectInvestmentStrategiesBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = arguments?.getString(CATEGORYNAME).toString()

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
        getViewModel().isSingleInvestment.set(arguments?.getBoolean(ISSINGLEINVESTMENT))
        getViewModel().isThematicInvestment.set(arguments?.getBoolean(ISTHEMATICINVESTMENT))

        btnContinue?.setOnClickListener {
            if (getViewModel().isThematicInvestment.get()==true){
                getViewModel().thirdlevel.get()?.let { it1 ->
                    context?.investmentStragiesDialog(it1) { thirdLevelCategory, amountLumpsum, amountSIP ->
                        investmentRecommendation(thirdLevelCategory.id, amountSIP, amountLumpsum, 0).observe(this,
                                android.arch.lifecycle.Observer { response ->
                                    val bundle = Bundle().apply {
                                        putInt("isFrom", 1)
                                        putString("sip", amountSIP.toString())
                                        putString("lumpsump", amountLumpsum.toString())
                                    }
                                    startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                    EventBus.getDefault().postSticky(getViewModel().secondlevel.get())
                                    EventBus.getDefault().postSticky(it1)
                                    EventBus.getDefault().postSticky(response?.data)
                                })
                    }
                }
            }else{
                val category = getViewModel().secondlevel.get()
                category?.thirdLevelCategory?.get(0)?.let { it1 ->
                    context?.investmentStragiesDialog(it1) { thirdLevelCategory, amountLumpsum, amountSIP ->
                        investmentRecommendation(thirdLevelCategory.id, amountSIP, amountLumpsum, 0).observe(this,
                                android.arch.lifecycle.Observer { response ->
                                    val bundle = Bundle().apply {
                                        putInt("isFrom", 2)
                                        putString("sip", amountSIP.toString())
                                        putString("lumpsump", amountLumpsum.toString())
                                        putBoolean("isThematic", false)
                                    }
                                    startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                    EventBus.getDefault().postSticky(category.thirdLevelCategory.get(0))
                                    EventBus.getDefault().postSticky(category)
                                    EventBus.getDefault().postSticky(response?.data)
                                })
                    }
                }
            }
        }

        tvNoteToInvestorsi?.setOnClickListener {
            getViewModel().noteToInvestors.get()?.let { isOpen ->
                getViewModel().noteToInvestors.set(!isOpen)
            }
        }
        //mPager?.setPagingEnabled(true)

        if (getViewModel().isSingleInvestment.get() == true) {
            // singleInvestmentUIFromSecondLevel()
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
        //removeStickyEvent(category)

        singleInvestmentUIFromSecondLevel()
    }

    @Subscribe(sticky = true)
    fun onThematicReceive(category: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory) {
        getViewModel().thirdlevel.set(category)
        //removeStickyEvent(category)

        singleInvestmentUIFromThirdLevel()
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

        mPager?.setWrapContentPageAdapter(R.layout.page_investment_options_item,
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
                                    putInt("isFrom", 1)
                                    putString("sip", amountSIP.toString())
                                    putString("lumpsump", amountLumpsum.toString())
                                }
                                startFragment(RecommendedBaseOnRiskLevelFragment.newInstance(bundle), R.id.frmContainer)
                                EventBus.getDefault().postSticky(category)
                                EventBus.getDefault().postSticky(item)
                                EventBus.getDefault().postSticky(response?.data)
                            })
                }
            }
            binder.executePendingBindings()
        }
        pageIndicator?.setViewPager(mPager)


    }

    private fun singleInvestmentUIFromSecondLevel() {
        getViewModel().secondlevel.get()?.let {
            tvRiskLevel?.setCompoundDrawablesWithIntrinsicBounds(it.riskLevelDrawable, 0, 0, 0)
            tvReturnLevel?.setCompoundDrawablesWithIntrinsicBounds(it.returnRiskDrawable, 0, 0, 0)
            tvReturnLevel?.text = it.returnLevel
            tvRiskLevel?.text = it.riskLevel

            getViewModel().singleInvestmentReturntype.set(it.returnLevel)
            getViewModel().singleInvestmentRiskType.set(it.riskLevel)
            getViewModel().singleInvestmentImg.set(it.categoryImage)
            getViewModel().singleInvestmentCategoryName.set(it.categoryName)
            getViewModel().singleInvestmentCategoryShortDes.set(it.categoryshortDesctiption)
            getViewModel().singleInvestmentCategoryDesc.set(it.categoryDesctiption)

            getViewModel().singleInvestmentRiskTypeVisible.set(it.riskLevelVisible)
            getViewModel().singleInvestmentReturntypeVisible.set(it.returnRiskVisible)
        }
    }

    private fun singleInvestmentUIFromThirdLevel() {
        getViewModel().thirdlevel.get()?.let {
            tvRiskLevel?.setCompoundDrawablesWithIntrinsicBounds(it.riskLevelDrawable, 0, 0, 0)
            tvReturnLevel?.setCompoundDrawablesWithIntrinsicBounds(it.returnRiskDrawable, 0, 0, 0)

            getViewModel().singleInvestmentReturntype.set(it.returnLevel)
            getViewModel().singleInvestmentRiskType.set(it.riskLevel)
            getViewModel().singleInvestmentImg.set(it.categoryImage)
            getViewModel().singleInvestmentCategoryName.set(it.categoryName)
            getViewModel().singleInvestmentCategoryShortDes.set(it.shortDescroption)
            getViewModel().singleInvestmentCategoryDesc.set(it.categoryDesctiption)
        }
    }
}
