package com.tarrakki.module.home


import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.tarrakki.*
import com.tarrakki.api.model.HomeData
import com.tarrakki.api.model.toDecrypt
import com.tarrakki.api.model.toJson
import com.tarrakki.databinding.FragmentHomeBinding
import com.tarrakki.module.ekyc.*
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.netbanking.NET_BANKING_PAGE
import com.tarrakki.module.netbanking.NetBankingFragment
import com.tarrakki.module.paymentmode.ISFROMTRANSACTIONMODE
import com.tarrakki.module.paymentmode.SUCCESSTRANSACTION
import com.tarrakki.module.paymentmode.SUCCESS_ORDERS
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.yourgoal.InitiateYourGoalFragment
import com.tarrakki.module.yourgoal.KEY_GOAL_ID
import com.tarrakki.module.zyaada.TarrakkiZyaadaFragment
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
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
        managePANBox()
        //ll_complete_verification?.visibility = if (context?.getKYCStatus()?.isNotBlank() == true || context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
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
        //openChromeTab()
        //openNetBankingPage()
    }

    private fun managePANBox() {
        ll_complete_verification?.visibility = if (context?.getKYCStatus()?.isNotBlank() == true || context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
    }

    private fun openNetBankingPage() {
        val data: String = "{\n" +
                "    \"org_data\": {\n" +
                "        \"data\": \"<html><head><title>Redirecting to Bank</title><style>.bodytxt4 {font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 12px;font-weight: bold;color: #666666;}.bodytxt {font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 13px;font-weight: normal;color: #000000;}.bullet1 {list-style-type:square;list-style-position: inside;list-style-image: none;font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 10px;font-weight: bold;color: #FF9900;}.bodytxt2 {font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 8pt;font-weight: normal;color: #333333;}A.sac2 {COLOR: #000000;font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 10px;font-weight: bold;text-decoration: none;}A.sac2:visited {COLOR: #314D5A; TEXT-DECORATION: none}A.sac2:hover {COLOR: #FF9900; TEXT-DECORATION: underline}</style></head><script language=JavaScript>var message=\\\"Function Disabled!\\\";function clickIE4(){if (event.button==2){return false;}}function clickNS4(e){if (document.layers||document.getElementById&&!document.all){if (e.which==2||e.which==3){return false;}}}if (document.layers){document.captureEvents(Event.MOUSEDOWN);document.onmousedown=clickNS4;}else if (document.all&&!document.getElementById){document.onmousedown=clickIE4;}document.oncontextmenu=new Function(\\\"return false\\\")</script><table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">  <tr>    <td align=\\\"left\\\" valign=\\\"top\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">        <tr>           <td align=\\\"center\\\" valign=\\\"middle\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">                           <tr>                <td  align=\\\"center\\\"></td>              </tr>              <tr>                <td height=\\\"85\\\" align=\\\"center\\\"><br>                  <table width=\\\"80%\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"1\\\" bgcolor=\\\"#CCCCCC\\\">                    <tr>                      <td bgcolor=\\\"#CCCCCC\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellpadding=\\\"6\\\" cellspacing=\\\"0\\\" bgcolor=\\\"#FFFFFF\\\">                          <tr>                             <td colspan=\\\"2\\\" align=\\\"left\\\" valign=\\\"bottom\\\"><span class=\\\"bodytxt4\\\">Your payment request is being processed...</span></td>                          </tr>                          <tr valign=\\\"top\\\">                             <td colspan=\\\"2\\\" align=\\\"left\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">                                <tr>                                   <td width=\\\"87%\\\" bgcolor=\\\"#cccccc\\\" height=\\\"1\\\" align=\\\"center\\\"></td>                                </tr>                              </table></td>                          </tr>                          <tr>                             <td width=\\\"60%\\\" align=\\\"left\\\" valign=\\\"bottom\\\"><table width=\\\"95%\\\" border=\\\"0\\\" cellpadding=\\\"1\\\" cellspacing=\\\"0\\\" bgcolor=\\\"#FFFFFF\\\">                                <tr>                                   <td align=\\\"right\\\" valign=\\\"top\\\"></td>                                  <td class=\\\"bodytxt\\\">&nbsp;</td>                                </tr>                                <tr>                                   <td height=\\\"19\\\"  align=\\\"right\\\" valign=\\\"top\\\"><li class=\\\"bullet1\\\"></li></td>                                  <td class=\\\"bodytxt2\\\">This is a secure payment                                     gateway using 128 bit SSL encryption.</td>                                </tr>                                <tr>                                   <td align=\\\"right\\\" valign=\\\"top\\\"> <li class=\\\"bullet1\\\"></li></td>                                  <td class=\\\"bodytxt2\\\" >When you submit the transaction,                                     the server will take about 1 to 5 seconds                                     to process, but it may take longer at certain                                     times. </td>                                </tr>                                <tr>                                   <td align=\\\"right\\\" valign=\\\"top\\\"><li class=\\\"bullet1\\\"></li></td>                                  <td class=\\\"bodytxt2\\\" >Please do not press \\\"Submit\\\"                                     button once again or the \\\"Back\\\" or \\\"Refresh\\\"                                     buttons. </td>                                </tr>                              </table></td>                            <td align=\\\"right\\\" valign=\\\"bottom\\\"><table width=\\\"80%\\\" border=\\\"0\\\" cellpadding=\\\"1\\\" cellspacing=\\\"0\\\" bgcolor=\\\"#FFFFFF\\\">                                <tr bgcolor=\\\"#FFFCF8\\\">                                   <td align=\\\"right\\\" bgcolor=\\\"#FFFFFF\\\"></td>                                </tr>                                <tr bgcolor=\\\"#FFFCF8\\\">                                   <td align=\\\"right\\\" valign=\\\"middle\\\" bgcolor=\\\"#FFFFFF\\\" class=\\\"bodytxt2\\\">&nbsp;</td>                                </tr>                                <tr bgcolor=\\\"#FFFCF8\\\">                                   <td align=\\\"right\\\" bgcolor=\\\"#FFFFFF\\\" class=\\\"bodytxt2\\\" >&nbsp;</td>                                </tr>                              </table></td>                          </tr>                        </table></td>                    </tr>                  </table>                                  </td>              </tr>            </table>                                           </td>        </tr>        </table></td>  </tr>  </table><body><form name=\\\"Bankfrm\\\" method=\\\"post\\\" action=\\\"https://shopping.icicibank.com/corp/BANKAWAY?IWQRYTASKOBJNAME=bay_mc_login&BAY_BANKID=ICI\\\">                 <input type = \\\"hidden\\\" name = \\\"MD\\\" value=\\\"P\\\">                              <input type = \\\"hidden\\\" name = \\\"PID\\\" value=\\\"000000001086\\\">                              <input type = \\\"hidden\\\" name = \\\"ES\\\" value=\\\"fmbIzNG9npDCU9C1lSctOmtJpRGAO4NagtRxVWZZcwkaEI0WjikRdJIFu0fBLDx/jFnWhPSFt8onCI5Rx30OhYdO9Zf6jcrbF4IfETqOVOXDMz9//eNdXILxcdsbg9N2LHPkKRUX+tKYEzxQiJt2x+P1dOS2G6xePFUYtTe9cnH/aNlIc3gr9WTb/+31zH4SlWx9VUKxhCuKxH0ryiUOpR3+YXwC/9bVuZA3D5P6LSq6FimWXa4VUYSZFf7zvAwI\\\">                              <input type = \\\"hidden\\\" name = \\\"SPID\\\" value=\\\"NA\\\">              </form></body><script>document.Bankfrm.submit();</script></html>\"\n" +
                "    },\n" +
                "    \"data\": \"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\",\n" +
                "    \"status\": {\n" +
                "        \"message\": \"\",\n" +
                "        \"code\": 1\n" +
                "    }\n" +
                "}"
        val json = JSONObject(data)
        startFragment(NetBankingFragment.newInstance(Bundle().apply {
            putSerializable(NET_BANKING_PAGE, JSONObject(json.optString("data").toDecrypt()).optString("data"))
            //putString(SUCCESSTRANSACTION, transaction.toString())
            //putString(SUCCESS_ORDERS, items.toJson())
            //isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
        }), R.id.frmContainer)
    }

    private fun openChromeTab() {
        val intentBuilder = CustomTabsIntent.Builder()
        // Begin customizing
        // set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimaryDark));
        intentBuilder.setShowTitle(true)
        // build custom tabs intent
        val customTabsIntent = intentBuilder.build()
        // launch the url
        try {
            // Here is a method that returns the chrome package name
            // Here is a method that returns the chrome package name
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName)
            }
            customTabsIntent.launchUrl(activity, Uri.parse("https://m-investor-onboarding.signzy.tech/icici_prudential2/5d9c3d151d3dce5774055e52/5eaad38f31d9ef1845b86ce6/1588253460/main?ns=icici_Tarrakki"))
        } catch (e: Exception) {
            context?.simpleAlert(getString(R.string.chrome_required_to_install)) {
                context?.openPlayStore(CustomTabsHelper.STABLE_PACKAGE)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getViewModel().getHomeData().observe(this, observerHomeData)
    }


    val observerHomeData = Observer<HomeData> {
        it?.let { apiResponse ->
            //ll_complete_verification?.visibility = if (context?.getKYCStatus()?.isNotBlank() == true || context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
            managePANBox()
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
                        getPANeKYCStatus(kyc.pan).observe(this, Observer {
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
                                            proceedVideoKYC(kyc)
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_on_hold))
                                            eventKYCDataLog(kyc, "03")
                                        }
                                    }
                                    kycStatus.contains("04") -> {
                                        if (kycStatus.firstOrNull()?.equals("04") == true) {
                                            proceedVideoKYC(kyc)
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_rejected))
                                            eventKYCDataLog(kyc, "04")
                                        }
                                    }
                                    kycStatus.contains("05") -> {
                                        proceedVideoKYC(kyc)
                                    }
                                    kycStatus.contains("06") -> {
                                        context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_deactivated))
                                        eventKYCDataLog(kyc, "06")
                                    }
                                    kycStatus.contains("12") -> {
                                        if (kycStatus.firstOrNull()?.equals("12") == true) {
                                            proceedVideoKYC(kyc)
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_registered))
                                            eventKYCDataLog(kyc, "12")
                                        }
                                    }
                                    kycStatus.contains("11") -> {
                                        if (kycStatus.firstOrNull()?.equals("11") == true) {
                                            proceedVideoKYC(kyc)
                                        } else {
                                            context?.simpleAlert(App.INSTANCE.getString(R.string.alert_under_process))
                                            eventKYCDataLog(kyc, "11")
                                        }
                                    }
                                    kycStatus.contains("13") -> {
                                        if (kycStatus.firstOrNull()?.equals("13") == true) {
                                            proceedVideoKYC(kyc)
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

    private fun proceedVideoKYC(kyc: KYCData) {
        edtPanNo?.text?.clear()
        startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
        postSticky(kyc)
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
