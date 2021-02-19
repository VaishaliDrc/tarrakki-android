package com.tarrakki.module.tarrakkipro

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.gocashfree.cashfreesdk.CFPaymentService
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.TarrakkiProAndEquityPricingResponse
import com.tarrakki.api.model.TarrakkiProPrice
import com.tarrakki.databinding.FragmentTarrakkiProBenefitsBinding
import com.tarrakki.databinding.RowEquityPlanBinding
import com.tarrakki.databinding.RowProBenefitItemBinding
import com.tarrakki.databinding.RowTarrakkiProPlanBindingImpl
import com.tarrakki.module.consumerloansliquilons.ConsumerLoansLiquiLoanFragment
import com.tarrakki.module.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_tarrakki_pro_benefits.*
import kotlinx.android.synthetic.main.row_tarrakki_pro_plan.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.getEmail
import org.supportcompact.ktx.getMobile
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import java.util.HashMap

class TarrakkiProBenefitsFragment : CoreFragment<TarrakkiProVM, FragmentTarrakkiProBenefitsBinding>() {
    lateinit var response: Observer<TarrakkiProAndEquityPricingResponse>
    private var planKey = "pro_"
    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.tarrakki_pro)

    override fun getLayout(): Int {
        return R.layout.fragment_tarrakki_pro_benefits
    }

    override fun createViewModel(): Class<out TarrakkiProVM> {
        return TarrakkiProVM::class.java
    }

    override fun setVM(binding: FragmentTarrakkiProBenefitsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        val divider = DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
        )
        val drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_benefits_divider
        )
        drawable?.let {
            divider.setDrawable(it)
        }
        rvProBenefits.addItemDecoration(divider)
        rvProBenefits?.setUpRecyclerView(R.layout.row_pro_benefit_item, getViewModel().proBenefitList) { item: ProbenefitList, binder: RowProBenefitItemBinding, position ->
            binder.menu = item
            binder.executePendingBindings()
        }


        response = Observer {
            it?.let { data ->
                data.tarrakkiProAndEquityPriceData?.let { price ->
                    rvProPlan.visibility = if (price.isTarrakkiPro!!) View.GONE else View.VISIBLE
                    tvChoosePlan.visibility = if (price.isTarrakkiPro!!) View.GONE else View.VISIBLE
                    tvMsgPlan.visibility = if (price.isTarrakkiPro!!) View.VISIBLE else View.GONE
                    tvMsgPlan.text = if (price.isTarrakkiPro!!) price.msgForTarrakkiPro else ""

                    rvProPlan?.setUpRecyclerView(R.layout.row_tarrakki_pro_plan, price.tarrakkiProPricing!!) { item: TarrakkiProPrice, binder: RowTarrakkiProPlanBindingImpl, position ->
                        binder.data = item
                        binder.root.clPlanFirst.setOnClickListener {
                            completePayment((item.price!!.toInt()*item.planDuration!!.toInt()),planKey+item.planDuration)
                        }
                        binder.executePendingBindings()
                    }

                }
            }
        }

        getViewModel().getTarrakkiAndWquityPricing().observe(this, response)

    }

    private fun completePayment(amount : Int, planType : String) {
        // Call Payment Token API
        //     val stage = "PROD"
        val stage = "TEST"
        getViewModel().getPaymentTokenAPI(amount,planType).observe(this, androidx.lifecycle.Observer {
            val params: MutableMap<String, String> = HashMap()

            params[CFPaymentService.PARAM_APP_ID] = "7996f54418f5378b2f70668f6997"  // STG APP ID
            //          params[CFPaymentService.PARAM_APP_ID] = "23824e9bcfb6946347bb6c9de42832"  // LIVE APP ID
            params[CFPaymentService.PARAM_ORDER_ID] = it.data.orderId
            params[CFPaymentService.PARAM_ORDER_AMOUNT] = it.data.amount
            params[CFPaymentService.PARAM_ORDER_NOTE] = title
            params[CFPaymentService.PARAM_CUSTOMER_NAME] = ""
            params[CFPaymentService.PARAM_CUSTOMER_PHONE] = "${App.INSTANCE.getMobile()}"
            params[CFPaymentService.PARAM_CUSTOMER_EMAIL] = "${App.INSTANCE.getEmail()}"
            params[CFPaymentService.PARAM_NOTIFY_URL] = it.data.callbackUrl
            params[CFPaymentService.PARAM_PAYMENT_OPTION] = ""
            params[CFPaymentService.PARAM_PAYMENT_MODES] = ""

            val cfPaymentService = CFPaymentService.getCFPaymentServiceInstance()
            cfPaymentService.setOrientation(0)
            cfPaymentService.doPayment(
                    requireActivity(),
                    params,
                    it.data.cftoken,
                    stage,
                    "#" + Integer.toHexString(ContextCompat.getColor(App.INSTANCE, R.color.white)),
                    "#" + Integer.toHexString(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimary)),
                    false
            )
        })

    }


    companion object {
        @JvmStatic
        fun newInstance() =
                TarrakkiProBenefitsFragment().apply {}
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: Bundle) {
        if (event.getString("txStatus").equals("SUCCESS", true)) {
            context?.simpleAlert(App.INSTANCE.getString(R.string.success_), event.getString("txMsg")?:"") {
                startFragment(HomeFragment.newInstance(), R.id.frmContainer)
            }
        } else {
            event.getString("txMsg")?.let {
                context?.simpleAlert(it)
            }
        }
        removeStickyEvent(event)
    }

}