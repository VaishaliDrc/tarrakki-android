package com.tarrakki.module.recommended


import androidx.lifecycle.Observer
import android.os.Bundle
import com.tarrakki.*
import com.tarrakki.api.model.HomeData
import com.tarrakki.api.model.InvestmentRecommendFundResponse
import com.tarrakki.databinding.FragmentRecommendedBaseOnRiskLevelBinding
import com.tarrakki.databinding.RowAmcListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.funddetails.FundDetailsFragment
import com.tarrakki.module.funddetails.ITEM_ID
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

        val viewModel = getViewModel()

        viewModel.isFrom.set(arguments?.getInt("isFrom"))
        viewModel.sipAmount.set(arguments?.getString("sip"))
        viewModel.lumpsumAmount.set(arguments?.getString("lumpsump"))

        viewModel.thirdLevelCategory.observe(this, Observer {
            if (arguments?.getBoolean("isThematic") == false) {

                it?.riskLevelDrawable?.let { it1 -> tvRiskLevel.setCompoundDrawablesWithIntrinsicBounds(it1, 0, 0, 0) }
                it?.returnRiskDrawable?.let { it1 -> tvReturnLevel.setCompoundDrawablesWithIntrinsicBounds(it1, 0, 0, 0) }

                viewModel.categoryImg.set(it?.categoryImage)
                viewModel.categoryDes.set(it?.categoryDesctiption)
                viewModel.categoryName.set(it?.categoryName)
                viewModel.categoryshortDes.set(it?.shortDescroption)

                tvRiskLevel.text = it?.riskLevel
                tvReturnLevel.text = it?.returnLevel

                tvRiskLevel.visibility = it?.riskLevelVisible!!
                tvReturnLevel.visibility = it.returnRiskVisible
            }
        })

        viewModel.recommendedFunds.observe(this, Observer {
            it?.let { it1 -> setAdapter(it1) }
        })

        viewModel.investment.observe(this, Observer { investment ->
            getBinding().invest = investment
            getBinding().executePendingBindings()
        })

        btnInvest?.setOnClickListener {
            viewModel.thirdLevelCategory.value?.id?.let { it1 ->
                investmentRecommendationToCart(it1, viewModel.sipAmount.get()!!,
                        viewModel.lumpsumAmount.get()!!, 1, false
                ).observe(this,
                        Observer { response ->
                            context?.simpleAlert(getString(R.string.cart_fund_added)) {
                                startFragment(CartFragment.newInstance(), R.id.frmContainer)
                            }
                        })
            }
        }

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
    fun onReceiveSecondCategory(category: HomeData.Data.Category.SecondLevelCategory) {
        getViewModel().secondLevelCategory.value = category
        EventBus.getDefault().removeStickyEvent(category)

        getViewModel().secondaryCategoryName.set(category.categoryName)
        getViewModel().secondaryCategoryImage.set(category.categoryImage)
        getViewModel().secondaryCategoryDes.set(category.categoryDesctiption)
        getViewModel().secondaryCategoryShortDes.set(category.categoryshortDesctiption)
        getViewModel().returnLevel.set(category.returnType)
        getViewModel().riskLevel.set(category.riskType)

        updateRiskReturnUI()
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

            binder.root.setOnClickListener {
                startFragment(FundDetailsFragment.newInstance(Bundle().apply { putString(ITEM_ID, "${item.id}") }), R.id.frmContainer)
            }
        }
    }

    private fun updateRiskReturnUI() {
        val viewModel = getViewModel()
        val returnRiskDrawable = getReturnLevelDrawable(viewModel.returnLevel.get().toString())
        val riskLevelDrawable = getRiskLevelDrawable(viewModel.riskLevel.get().toString())
        val returnRiskVisible = getReturnLevelVisibility(viewModel.returnLevel.get().toString())
        val riskLevelVisible = getRiskLevelVisibility(viewModel.riskLevel.get().toString())

        tvRiskLevelSec.setCompoundDrawablesWithIntrinsicBounds(riskLevelDrawable, 0, 0, 0)
        tvRiskLevelSec.visibility = riskLevelVisible
        tvReturnLevelSec.setCompoundDrawablesWithIntrinsicBounds(returnRiskDrawable, 0, 0, 0)
        tvReturnLevelSec.visibility = returnRiskVisible

        tvRiskLevelSec.text = getRiskLevel(viewModel.riskLevel.get())
        tvReturnLevelSec.text = getReturnLevel(viewModel.returnLevel.get())
    }
}