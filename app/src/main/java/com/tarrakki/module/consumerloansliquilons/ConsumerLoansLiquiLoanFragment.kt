package com.tarrakki.module.consumerloansliquilons

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.GetLiquiLoansSchemaBaseResponse
import com.tarrakki.api.model.Scheme
import com.tarrakki.databinding.FragmentConsumerLoanLiquiBinding
import com.tarrakki.databinding.RowSchemaItemBinding
import com.tarrakki.module.support.SupportFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_consumer_loan_liqui.*
import kotlinx.android.synthetic.main.fragment_consumer_loan_liqui.mRefresh
import kotlinx.android.synthetic.main.fragment_home.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.events.Event
import org.supportcompact.ktx.getWhatsAppURI
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toComaSeparate
import org.supportcompact.ktx.toast
import org.supportcompact.utilise.EqualSpacingItemDecoration

class ConsumerLoansLiquiLoanFragment : CoreFragment<ConsumerLoansLiquiLoanVM, FragmentConsumerLoanLiquiBinding>() {


    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.invest_in_consumer_loans)

    override fun getLayout(): Int {
        return R.layout.fragment_consumer_loan_liqui
    }

    override fun createViewModel(): Class<out ConsumerLoansLiquiLoanVM> {
        return ConsumerLoansLiquiLoanVM::class.java
    }

    override fun setVM(binding: FragmentConsumerLoanLiquiBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    val observerLiquiloansSchemeData = Observer<GetLiquiLoansSchemaBaseResponse> {
        it?.let { apiResponse ->
            mRefresh?.isRefreshing = false

            if(it.data.data_points.isNotEmpty()){
                var borrowers =  it.data.data_points.get(0).borrowers

                        if(borrowers.contains("+"))
                            borrowers = borrowers.replace("+","")

                borrowers = borrowers.toDoubleOrNull()?.toComaSeparate().toString()+"+"

                var lenders =  it.data.data_points.get(0).lenders

                        if(lenders.contains("+"))
                            lenders = lenders.replace("+","")

                lenders = lenders.toDoubleOrNull()?.toComaSeparate().toString()+"+"

                getViewModel().borrowers.set(borrowers)
                getViewModel().disbursementMonth.set(it.data.data_points.get(0).disbursement_month)
                getViewModel().disbursements.set(it.data.data_points.get(0).disbursements)
                getViewModel().totalDisbursements.set(it.data.data_points.get(0).total_disbursements)
                getViewModel().lenders.set(lenders)
                getViewModel().gross_npa.set(it.data.data_points.get(0).gross_npa)
            }


            rvScheme.setUpRecyclerView(
                    R.layout.row_schema_item,
                    getViewModel().schemaList) { item: Scheme, binder: RowSchemaItemBinding, position ->
                binder.setVariable(BR.schemeItem, item)


                binder.executePendingBindings()
            }

            rvScheme.visibility = View.VISIBLE
        }
    }

    private fun chatWhatsapp() {
        if (App.INSTANCE.getWhatsAppURI()?.isNotEmpty() == true) {
            try {
                val packageManager = requireActivity().packageManager
                val i = Intent(Intent.ACTION_VIEW)
                val phone = 917573059595
                val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
                if (isWhatsappInstalled) {
                    try {
                        val url = "https://wa.me/$phone"
                        i.setPackage("com.whatsapp")
                        i.data = Uri.parse(App.INSTANCE.getWhatsAppURI())
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

    override fun createReference() {

        setHasOptionsMenu(true)

        rvScheme.isFocusable = false
        rvScheme.isNestedScrollingEnabled = false


        mRefresh?.setOnRefreshListener {
            getViewModel().getLiquiloansSchemaAPI().observe(this, observerLiquiloansSchemeData)
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })

        getViewModel().getLiquiloansSchemaAPI().observe(this, observerLiquiloansSchemeData)

        tvWhatsappToInvest?.setOnClickListener {
            chatWhatsapp()
        }

        img_arrow_info?.setOnClickListener {
            getViewModel().showInvestInfo.set(!getViewModel().showInvestInfo.get()!!)
        }
        tvLiquiloansFAQ?.setOnClickListener {
            /*startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
            postSticky(Event.LIQUILOANS_FAQ)*/
            startFragment(SupportFragment.newInstance(), R.id.frmContainer)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ConsumerLoansLiquiLoanFragment().apply { arguments = basket }
    }
}
