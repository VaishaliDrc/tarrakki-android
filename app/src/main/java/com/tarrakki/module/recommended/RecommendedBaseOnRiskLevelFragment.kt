package com.tarrakki.module.recommended


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentRecommendedBaseOnRiskLevelBinding
import com.tarrakki.databinding.RowAmcListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.investmentstrategies.InvestmentOption
import kotlinx.android.synthetic.main.fragment_recommended_base_on_risk_level.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [RecommendedBaseOnRiskLevelFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

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
        getViewModel().investment.observe(this, Observer { investment ->
            getBinding().invest = investment
            getBinding().executePendingBindings()
        })
        rvAMCList?.isFocusable = false
        rvAMCList?.isNestedScrollingEnabled = false
        rvAMCList?.setUpRecyclerView(R.layout.row_amc_list_item, getViewModel().AMCList) { item: AMC, binder: RowAmcListItemBinding, position ->
            binder.amc = item
            binder.executePendingBindings()
        }
        btnInvest?.setOnClickListener {
            ///startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: InvestmentOption) {
        getViewModel().investment.value = data
        EventBus.getDefault().removeStickyEvent(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket basket as Bundle.
         * @return A new instance of fragment RecommendedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RecommendedBaseOnRiskLevelFragment().apply { arguments = basket }
    }
}