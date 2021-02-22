package com.tarrakki.module.equityadvisory


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gocashfree.cashfreesdk.CFPaymentService
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.MySipApiResponse
import com.tarrakki.api.model.MySipData
import com.tarrakki.api.model.TarrakkiProAndEquityPricingResponse
import com.tarrakki.api.model.TarrakkiProPrice
import com.tarrakki.databinding.FragmentEquityAdvisoryBinding
import com.tarrakki.databinding.RowEquityPlanBinding
import com.tarrakki.databinding.RowWhyTarrakkiItemBinding
import com.tarrakki.fcm.eventTZDebitCardRequest
import com.tarrakki.module.home.HomeFragment
import com.tarrakki.module.portfolio.StopSIP
import com.tarrakki.module.redeem.RedeemStopConfirmationFragment
import com.tarrakki.module.support.SupportFragment
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.stopFundPortfolioDialog
import kotlinx.android.synthetic.main.fragment_completed_transactions.*
import kotlinx.android.synthetic.main.fragment_completed_transactions.tvNoItem
import kotlinx.android.synthetic.main.fragment_equity_advisory.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.mRefresh
import kotlinx.android.synthetic.main.fragment_my_sip.*
import kotlinx.android.synthetic.main.row_tarrakki_pro_plan.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.BR
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*
import java.util.HashMap


class EquityAdvisoryFragment : CoreFragment<EquityAdvisoryVM, FragmentEquityAdvisoryBinding>() {

    private var planKey = "eq_"
    lateinit var response: Observer<TarrakkiProAndEquityPricingResponse>
    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.equity_advisory)

    override fun getLayout(): Int {
        return R.layout.fragment_equity_advisory
    }

    override fun createViewModel(): Class<out EquityAdvisoryVM> {
        return EquityAdvisoryVM::class.java
    }

    override fun setVM(binding: FragmentEquityAdvisoryBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }


    override fun createReference() {

        tvWhatIsEquity?.setOnClickListener {
            getViewModel().equityAdvisoryVisibility.get()?.let {
                getViewModel().equityAdvisoryVisibility.set(!it)
            }
        }


        rvWhyTarrakki?.setUpRecyclerView(R.layout.row_why_tarrakki_item, getViewModel().whyTarrakkiList) { item: WhyTarrakkiList, binder: RowWhyTarrakkiItemBinding, position ->
            binder.menu = item
            binder.executePendingBindings()
        }

        rvWhatYouWillGet?.setUpRecyclerView(R.layout.row_why_tarrakki_item, getViewModel().equityBenefitList) { item: WhyTarrakkiList, binder: RowWhyTarrakkiItemBinding, position ->
            binder.menu = item
            binder.executePendingBindings()
        }

        tvFAQs?.setOnClickListener {
            startFragment(SupportFragment.newInstance(), R.id.frmContainer)
        }

        tvSetCallWithAdviser?.setOnClickListener {
            chatWhatsApp()
        }


        response = Observer {
            it?.let { data ->
                data.tarrakkiProAndEquityPriceData?.let { price ->
                    rvEquityPlan.visibility = if (price.isEquityAdvisory!!) View.GONE else View.VISIBLE
                    tvMsgPlan.visibility = if (price.isEquityAdvisory!!) View.VISIBLE else View.GONE
                    tvMsgPlan.text = if (price.isEquityAdvisory!!) price.msgForEquityAdvisory else ""

                    rvEquityPlan?.setUpRecyclerView(R.layout.row_equity_plan,  price.equityAdvisoryPricing!!) { item: TarrakkiProPrice, binder: RowEquityPlanBinding, position ->
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

    private fun chatWhatsApp() {
        if (App.INSTANCE.getWhatsAppURI()?.isNotEmpty() == true) {
            try {
                val packageManager = requireActivity().packageManager
                val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
                if (isWhatsappInstalled) {
                    try {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(App.INSTANCE.getWhatsAppURI()))
                        i.setPackage("com.whatsapp")
                        if (i.resolveActivity(packageManager) != null) {
                            context?.startActivity(i)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val uri = Uri.parse("market://details?id=com.whatsapp")
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    Toast.makeText(activity, "WhatsApp not Installed",
                            Toast.LENGTH_SHORT).show()
                    startActivity(goToMarket)
                }
            } catch (e: Exception) {
            }
        } else {
            toast("Please reload the home screen once to use this feature.")
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager? = activity?.getPackageManager()
        var appInstalled = false
        appInstalled = try {
            pm?.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return appInstalled
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                EquityAdvisoryFragment().apply {}
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: Bundle) {
        if (event.getString("txStatus").equals("SUCCESS", true)) {
            context?.simpleAlert(App.INSTANCE.getString(R.string.success_), event.getString("txMsg")?:"") {
                startFragment(HomeFragment.newInstance(), R.id.frmContainer)
            }
        } else {
            event.getString("txMsg") ?.let {
                context?.simpleAlert(it)
            }
        }
        removeStickyEvent(event)
    }


}