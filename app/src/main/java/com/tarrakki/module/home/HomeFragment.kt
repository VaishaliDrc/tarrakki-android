package com.tarrakki.module.home


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import com.tarrakki.*
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentHomeBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.KYCRegistrationAFragment
import com.tarrakki.module.ekyc.isPANCard
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.EqualSpacingItemDecoration

const val CATEGORYNAME = "category_name"
const val ISSINGLEINVESTMENT = "category_single_investment"
const val ISTHEMATICINVESTMENT = "category_thematic_investment"


class HomeFragment : CoreFragment<HomeVM, FragmentHomeBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.home)

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun createViewModel(): Class<out HomeVM> {
        return HomeVM::class.java
    }

    override fun setVM(binding: FragmentHomeBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        ll_complete_verification?.visibility = if (context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
    }

    @Subscribe(sticky = true)
    fun onEventData(event: Event) {
        when (event) {
            Event.ISGOALADDED -> {
                getViewModel().getHomeData().observe(this, observerHomeData)
            }
        }
    }

    val observerHomeData = Observer<HomeData> {
        it?.let { apiResponse ->

            rvHomeItem.setUpMultiViewRecyclerAdapter(getViewModel().homeSections) { item, binder, position ->
                binder.setVariable(BR.section, item)
                binder.setVariable(BR.isHome, true)
                binder.setVariable(BR.onViewAll, View.OnClickListener {
                    if (item is HomeSection)
                        when ("${item.title}") {
                            "Set a Goal" -> {
                                startFragment(GoalFragment.newInstance(), R.id.frmContainer)
                            }
                            else -> {
                                val bundle = Bundle().apply {
                                    putString(CATEGORYNAME, item.title)
                                }
                                startFragment(InvestmentStrategiesFragment.newInstance(bundle), R.id.frmContainer)
                                item.category?.let { postSticky(it) }
                            }
                        }
                })
                binder.executePendingBindings()
            }
            rvHomeItem.visibility = View.VISIBLE
        }
    }

    override fun createReference() {
        setHasOptionsMenu(true)

        rvHomeItem?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_item)))

        rvHomeItem.isFocusable = false
        rvHomeItem.isNestedScrollingEnabled = false

        App.INSTANCE.widgetsViewModel.observe(this, Observer { item ->
            item?.let {
                if (item is HomeData.Data.Goal) {
                    startFragment(InitiateYourGoalFragment.newInstance(Bundle().apply { putString(KEY_GOAL_ID, "${item.id}") }), R.id.frmContainer)
                } else if (item is HomeData.Data.Category.SecondLevelCategory) {
                    activity?.onInvestmentStrategies(item)
                }
                App.INSTANCE.widgetsViewModel.value = null
            }
        })
        edtPanNo?.applyPAN()
        btnCheck?.setOnClickListener {
            if (edtPanNo.length() == 0) {
                //startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                context?.simpleAlert(getString(R.string.alert_req_pan_number))
            } else if (!isPANCard(edtPanNo.text.toString())) {
                context?.simpleAlert(getString(R.string.alert_valid_pan_number))
            } else {
                it.dismissKeyboard()
                val kyc = KYCData(edtPanNo.text.toString(), "${App.INSTANCE.getEmail()}", "${App.INSTANCE.getMobile()}")
                getEncryptedPasswordForCAMPSApi().observe(this, android.arch.lifecycle.Observer {
                    it?.let { password ->
                        getPANeKYCStatus(password, kyc.pan).observe(this, android.arch.lifecycle.Observer {
                            it?.let { kycStatus ->
                                edtPanNo?.text?.clear()
                                when {
                                    kycStatus.contains("02") || kycStatus.contains("01") -> {
                                        getEKYCData(password, kyc).observe(this, Observer { data ->
                                            data?.let { kyc ->
                                                startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                                                postSticky(kyc)
                                            }
                                        })
                                    }
                                    kycStatus.contains("03") -> context?.simpleAlert("Your KYC is on hold")
                                    kycStatus.contains("04") -> context?.simpleAlert("Your KYC is kyc rejected")
                                    kycStatus.contains("05") -> context?.simpleAlert("Your KYC is not available")
                                    kycStatus.contains("06") -> context?.simpleAlert("Your KYC is deactivate")
                                    kycStatus.contains("12") -> context?.simpleAlert("KYC REGISTERED - Incomplete KYC (Existing / OLD Record)")
                                    kycStatus.contains("11") -> context?.simpleAlert("UNDER_PROCESS - Incomplete KYC (Existing / OLD Record)")
                                    kycStatus.contains("13") -> context?.simpleAlert("ON HOLD- Incomplete KYC (Existing / OLD Record) 22- CVL MF KYC")
                                    kycStatus.contains("99") -> context?.simpleAlert("If specific KRA web service is not reachable")
                                    else -> {
                                        context?.simpleAlert("If specific KRA web service is not reachable")
                                    }
                                }
                            }

                        })
                    }
                })
                /*checkKYCStatus(kyc).observe(this, Observer {
                    it?.let { html ->
                        //<input type='hidden' name='result' value='N|AJNPV8599B|KS101|The KYC for this PAN is not complete' />
                        try {
                            val doc = Jsoup.parse(html)
                            val values = doc.select("input[name=result]").attr("value").split("|")
                            if (values.isNotEmpty() && values.contains("N") && values.contains("KS101")) {
                                startFragment(EKYCFragment.newInstance(), R.id.frmContainer)
                                postSticky(kyc)
                            } else {
                                //post(ShowError(values[3]))
                                startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                                postSticky(kyc)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        edtPanNo?.text?.clear()
                    }
                })*/
            }
        }

        tvWhyTarrakkii?.setOnClickListener {
            getViewModel().whayTarrakki.get()?.let {
                getViewModel().whayTarrakki.set(!it)
            }
        }

        btnIdle?.setOnClickListener {
            context?.simpleAlert(getString(R.string.coming_soon))
        }

        tvViewPortfolio?.setOnClickListener {
            //context?.simpleAlert("Portfolio is still under development so you will be able to test it in the next build.")

            startFragment(PortfolioFragment.newInstance(), R.id.frmContainer)
        }

        mRefresh?.setOnRefreshListener {
            getViewModel().getHomeData(true).observe(this, observerHomeData)
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })
        getViewModel().getHomeData().observe(this, observerHomeData)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            it?.let {
                tvCartCount?.cartCount(it)
            }
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
    }*/

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = HomeFragment().apply { arguments = basket }
    }
}
