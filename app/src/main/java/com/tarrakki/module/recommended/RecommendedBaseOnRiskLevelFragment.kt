package com.tarrakki.module.recommended


import android.arch.lifecycle.Observer
import android.os.Bundle
import com.tarrakki.*
import com.tarrakki.api.model.HomeData
import com.tarrakki.api.model.InvestmentRecommendFundResponse
import com.tarrakki.databinding.FragmentRecommendedBaseOnRiskLevelBinding
import com.tarrakki.databinding.RowAmcListItemBinding
import com.tarrakki.module.cart.CartFragment
import kotlinx.android.synthetic.main.fragment_recommended_base_on_risk_level.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import java.util.*


class RecommendedBaseOnRiskLevelFragment : CoreFragment<RecommendedVM, FragmentRecommendedBaseOnRiskLevelBinding>() {


    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.our_recommended)

    override fun getLayout(): Int {
        return R.layout.fragment_recommended_base_on_risk_level
    }

    override fun createViewModel(): Class<out RecommendedVM> {
        return RecommendedVM::class.java
    }

    override fun setVM(binding: FragmentRecommendedBaseOnRiskLevelBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        EventBus.getDefault().register(this)

        getViewModel().secondaryCategoryName.set(arguments?.getString("categoryName"))
        getViewModel().secondaryCategoryImage.set(arguments?.getString("categoryImage"))
        getViewModel().secondaryCategoryDes.set(arguments?.getString("categoryDes"))
        getViewModel().secondaryCategoryShortDes.set(arguments?.getString("categoryshortDes"))
        getViewModel().returnLevel.set(arguments?.getString("returnLevel"))
        getViewModel().riskLevel.set(arguments?.getString("riskLevel"))
        getViewModel().isFrom.set(arguments?.getInt("isFrom"))
        getViewModel().sipAmount.set(arguments?.getInt("sip"))
        getViewModel().lumpsumAmount.set(arguments?.getInt("lumpsump"))

        getViewModel().thirdLevelCategory.observe(this, Observer {
            it?.riskLevelDrawable?.let { it1 -> tvRiskLevel.setCompoundDrawablesWithIntrinsicBounds(it1, 0, 0, 0) }
            it?.returnRiskDrawable?.let { it1 -> tvReturnLevel.setCompoundDrawablesWithIntrinsicBounds(it1, 0, 0, 0) }

            getViewModel().categoryImg.set(it?.categoryImage)
            getViewModel().categoryDes.set(it?.categoryDesctiption)
            getViewModel().categoryName.set(it?.categoryName)
            getViewModel().categoryshortDes.set(it?.shortDescroption)

            tvRiskLevel.text = it?.riskLevel
            tvReturnLevel.text = it?.returnLevel

            tvRiskLevel.visibility = it?.riskLevelVisible!!
            tvReturnLevel.visibility = it.returnRiskVisible
        })

        getViewModel().recommendedFunds.observe(this, Observer {
            it?.let { it1 -> setAdapter(it1) }
        })

        getViewModel().investment.observe(this, Observer { investment ->
            getBinding().invest = investment
            getBinding().executePendingBindings()
        })

        btnInvest?.setOnClickListener {
            //context?.investmentStragiesDialog(getViewModel().thirdLevelCategory.value as HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory) { thirdLevelCategory, amountLumpsum, amountSIP ->
            getViewModel().thirdLevelCategory.value?.id?.let { it1 ->
                investmentRecommendationToCart(it1, getViewModel().sipAmount.get()!!,
                        getViewModel().lumpsumAmount.get()!!, 1, false
                ).observe(this,
                        android.arch.lifecycle.Observer { response ->
                            context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                startFragment(CartFragment.newInstance(), R.id.frmContainer)
                            }
                        })
            }
            //}
        }

        val returnRiskDrawable = getReturnLevelDrawable(getViewModel().returnLevel.get().toString())
        val riskLevelDrawable = getRiskLevelDrawable(getViewModel().riskLevel.get().toString())
        val returnRiskVisible = getReturnLevelVisibility(getViewModel().returnLevel.get().toString())
        val riskLevelVisible = getRiskLevelVisibility(getViewModel().riskLevel.get().toString())

        tvRiskLevelSec.setCompoundDrawablesWithIntrinsicBounds(riskLevelDrawable,0,0,0)
        tvRiskLevelSec.visibility = riskLevelVisible
        tvReturnLevelSec.setCompoundDrawablesWithIntrinsicBounds(returnRiskDrawable,0,0,0)
        tvReturnLevelSec.visibility = returnRiskVisible

        tvRiskLevelSec.text = getRiskLevel(getViewModel().riskLevel.get())
        tvReturnLevelSec.text = getReturnLevel(getViewModel().returnLevel.get())

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RecommendedBaseOnRiskLevelFragment().apply { arguments = basket }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceiveThirdCategory(category: HomeData.Data.Category.SecondLevelCategory.ThirdLevelCategory) {
        getViewModel().thirdLevelCategory.value = category
        EventBus.getDefault().removeStickyEvent(category)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceiveRecommendedFunds(funds: List<InvestmentRecommendFundResponse.Data>) {
        getViewModel().recommendedFunds.value = funds
        EventBus.getDefault().removeStickyEvent(funds)
    }

    fun setAdapter(funds: List<InvestmentRecommendFundResponse.Data>) {
        rvAMCList?.isFocusable = false
        rvAMCList?.isNestedScrollingEnabled = false
        rvAMCList?.setUpRecyclerView(R.layout.row_amc_list_item,
                funds as ArrayList<InvestmentRecommendFundResponse.Data>) { item: InvestmentRecommendFundResponse.Data, binder: RowAmcListItemBinding, position ->
            binder.vm = item
            binder.executePendingBindings()
        }
    }
}