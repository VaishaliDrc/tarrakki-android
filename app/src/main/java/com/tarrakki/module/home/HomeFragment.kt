package com.tarrakki.module.home


import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.lifecycle.Observer
import com.tarrakki.*
import com.tarrakki.api.model.HomeData
import com.tarrakki.databinding.FragmentHomeBinding
import com.tarrakki.module.ekyc.*
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import com.tarrakki.module.zyaada.TarrakkiZyaadaFragment
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.Event
import org.supportcompact.events.EventData
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
        ll_complete_verification?.visibility = if (context?.getKYCStatus()?.isNotBlank() == true || context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
        if (context?.isAskForSecureLock() == false && !getViewModel().isShowingSecurityDialog) {
            getViewModel().isShowingSecurityDialog = true
            val km = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (km.isKeyguardSecure && getViewModel().isAskedForSecurityLock) {
                App.INSTANCE.setAppIsLock(true)
                App.INSTANCE.setAskForSecureLock(true)
                App.INSTANCE.isAuthorise.value = true
                return
            }
            context?.confirmationDialog(getString(R.string.do_you_want_to_enable_app_security),
                    btnPositiveClick = {
                        getViewModel().isShowingSecurityDialog = false
                        getViewModel().isAskedForSecurityLock = true
                        if (!km.isKeyguardSecure) {
                            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                            startActivity(intent)
                        } else {
                            App.INSTANCE.setAppIsLock(true)
                            App.INSTANCE.setAskForSecureLock(true)
                            App.INSTANCE.isAuthorise.value = true
                        }
                    },
                    btnNegativeClick = {
                        App.INSTANCE.setAskForSecureLock(true)
                    }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        getViewModel().getHomeData().observe(this, observerHomeData)
    }


    val observerHomeData = Observer<HomeData> {
        it?.let { apiResponse ->
            ll_complete_verification?.visibility = if (context?.getKYCStatus()?.isNotBlank() == true || context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
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
            getViewModel().redirectToInvestmentStratergy.value?.let {
                getViewModel().redirectToInvestmentStratergy.value = it
            }
        }
    }

    override fun createReference() {
        //context?.setKYCStatus("REDO")
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

        getViewModel().redirectToInvestmentStratergy.observe(this, Observer {
            it?.let { id ->
                val result = getViewModel().homeSections
                        .filterIsInstance<HomeSection>()
                        .firstOrNull { it.title != "Set a Goal" }?.homeItems
                        ?.filterIsInstance<HomeData.Data.Category.SecondLevelCategory>()
                        ?.firstOrNull { "${it.id}" == "$id" }
                result?.let { item ->
                    activity?.onInvestmentStrategies(item)
                    getViewModel().redirectToInvestmentStratergy.value = null
                }
            }
        })

        //edtPanNo?.applyPAN()
        btnCheck?.setOnClickListener {
            if (edtPanNo.length() == 0) {
                context?.simpleAlert(getString(R.string.alert_req_pan_number))
            } else if (!isPANCard(edtPanNo.text.toString())) {
                context?.simpleAlert(getString(R.string.alert_valid_pan_number))
            } else {
                it.dismissKeyboard()
                val kyc = KYCData(edtPanNo.text.toString(), "${App.INSTANCE.getEmail()}", "${App.INSTANCE.getMobile()}")
                getEncryptedPasswordForCAMPSApi().observe(this, Observer {
                    it?.let { password ->
                        getPANeKYCStatus(password, kyc.pan).observe(this, Observer {
                            it?.let { kycStatus ->
                                when {
                                    kycStatus.contains("02") || kycStatus.contains("01") -> {
                                        getEKYCData(password, kyc).observe(this, Observer { data ->
                                            data?.let { kyc ->
                                                context?.confirmationDialog(
                                                        title = getString(R.string.pls_select_your_tax_status),
                                                        msg = "Note: Minor are individuals born after ${getDate(18).convertTo("dd MMM, yyyy")}.",
                                                        btnPositive = getString(R.string.major),
                                                        btnNegative = getString(R.string.minor),
                                                        btnPositiveClick = {
                                                            edtPanNo?.text?.clear()
                                                            startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                                                            postSticky(kyc)
                                                        },
                                                        btnNegativeClick = {
                                                            edtPanNo?.text?.clear()
                                                            kyc.guardianName = "${kyc.nameOfPANHolder}"
                                                            startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                                                            postSticky(kyc)
                                                        }
                                                )
                                            }
                                        })
                                    }
                                    kycStatus.contains("03") -> {
                                        if (kycStatus.firstOrNull()?.equals("03") == true) {
                                            apiApplyForNewKYC().observe(this, Observer {
                                                it?.let {
                                                    edtPanNo?.text?.clear()
                                                    kyc.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                                                    startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                                    postSticky(kyc)
                                                }
                                            })
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_on_hold))
                                            eventKYCDataLog(kyc, "03")
                                        }
                                    }
                                    kycStatus.contains("04") -> {
                                        if (kycStatus.firstOrNull()?.equals("04") == true) {
                                            apiApplyForNewKYC().observe(this, Observer {
                                                it?.let {
                                                    edtPanNo?.text?.clear()
                                                    kyc.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                                                    startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                                    postSticky(kyc)
                                                }
                                            })
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_rejected))
                                            eventKYCDataLog(kyc, "04")
                                        }
                                    }
                                    kycStatus.contains("05") -> {
                                        apiApplyForNewKYC().observe(this, Observer {
                                            it?.let {
                                                edtPanNo?.text?.clear()
                                                kyc.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                                                startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                                postSticky(kyc)
                                            }
                                        })
                                        /*context?.simpleAlert(App.INSTANCE.getString(R.string.alert_not_available))
                                        eventKYCDataLog(kyc, "05")*/
                                    }
                                    kycStatus.contains("06") -> {
                                        context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_deactivated))
                                        eventKYCDataLog(kyc, "06")
                                    }
                                    kycStatus.contains("12") -> {
                                        if (kycStatus.firstOrNull()?.equals("12") == true) {
                                            apiApplyForNewKYC().observe(this, Observer {
                                                it?.let {
                                                    edtPanNo?.text?.clear()
                                                    kyc.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                                                    startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                                    postSticky(kyc)
                                                }
                                            })
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_registered))
                                            eventKYCDataLog(kyc, "12")
                                        }
                                    }
                                    kycStatus.contains("11") -> {
                                        if (kycStatus.firstOrNull()?.equals("11") == true) {
                                            apiApplyForNewKYC().observe(this, Observer {
                                                it?.let {
                                                    edtPanNo?.text?.clear()
                                                    kyc.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                                                    startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                                    postSticky(kyc)
                                                }
                                            })
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_under_process))
                                            eventKYCDataLog(kyc, "11")
                                        }
                                    }
                                    kycStatus.contains("13") -> {
                                        if (kycStatus.firstOrNull()?.equals("13") == true) {
                                            apiApplyForNewKYC().observe(this, Observer {
                                                it?.let {
                                                    edtPanNo?.text?.clear()
                                                    startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                                    postSticky(kyc)
                                                }
                                            })
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_on_hold_due_to_incomplete))
                                            eventKYCDataLog(kyc, "13")
                                        }
                                    }
                                    kycStatus.contains("99") -> {
                                        context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_server_not_reachable))
                                        eventKYCDataLog(kyc, "99")
                                    }
                                    else -> {
                                        context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_server_not_reachable))
                                        eventKYCDataLog(kyc, "unknown")
                                    }
                                }
                            }

                        })
                    }
                })
            }
        }

        tvWhyTarrakkii?.setOnClickListener {
            getViewModel().whayTarrakki.get()?.let {
                getViewModel().whayTarrakki.set(!it)
            }
        }

        clTarrakkiZyaada?.setOnClickListener {
            startFragment(TarrakkiZyaadaFragment.newInstance(), R.id.frmContainer)
        }

        tvViewPortfolio?.setOnClickListener {
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

        checkAppUpdate().observe(this, Observer {
            it?.data?.let {
                val versionName = BuildConfig.VERSION_NAME
                if (!versionName.equals(it.version, true)) {
                    if (it.forceUpdate == true) {
                        context?.appForceUpdate(getString(R.string.app_update), "${it.message}", getString(R.string.update)) {
                            context?.openPlayStore()
                        }
                    } else {
                        context?.confirmationDialog(getString(R.string.app_update), "${it.message}", btnNegative = getString(R.string.cancel), btnPositive = getString(R.string.update),
                                btnPositiveClick = {
                                    context?.openPlayStore()
                                }
                        )
                    }
                }
            }
        })
    }

    @Subscribe(sticky = true)
    override fun onEvent(event: Event) {
        when (event) {
            Event.OPEN_TARRAKKI_ZYAADA -> {
                clTarrakkiZyaada?.performClick()
                removeStickyEvent(event)
            }
            else -> super.onEvent(event)
        }
    }

    @Subscribe(sticky = true)
    override fun onEvent(event: EventData) {
        when (event.event) {
            Event.OPEN_MY_CART -> {
                getViewModel().redirectToInvestmentStratergy.value = event.message
                removeStickyEvent(event)
            }
            else -> super.onEvent(event)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = HomeFragment().apply { arguments = basket }
    }
}
