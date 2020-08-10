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
import com.tarrakki.databinding.FragmentHomeBinding
import com.tarrakki.module.ekyc.*
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.investmentstrategies.InvestmentStrategiesFragment
import com.tarrakki.module.netbanking.NET_BANKING_PAGE
import com.tarrakki.module.netbanking.NetBankingFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.webview.WebViewFragment
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
        //openTestWebview()
    }

    private fun managePANBox() {
        ll_complete_verification?.visibility = if (context?.getKYCStatus()?.isNotBlank() == true || context?.isCompletedRegistration() == true || context?.isKYCVerified() == true) View.GONE else View.VISIBLE
    }

    private fun openTestWebview() {
        startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
        postSticky(Event.REFRESH)
    }

    private fun openNetBankingPage() {
        val data: String = "{\n" +
                "  \"data\": \"\\n\\n\\n<html>\\n<head><title>Redirecting to Bank</title>\\n<style>\\n\\n.bodytxt4 {\\n\\n\\tfont-family: Verdana, Arial, Helvetica, sans-serif;\\n\\tfont-size: 12px;\\n\\tfont-weight: bold;\\n\\tcolor: #666666;\\n}\\n.bodytxt {\\n\\tfont-family: Verdana, Arial, Helvetica, sans-serif;\\n\\tfont-size: 13px;\\n\\tfont-weight: normal;\\n\\tcolor: #000000;\\n\\n}\\n.bullet1 {\\n\\n\\tlist-style-type:\\tsquare;\\n\\tlist-style-position: inside;\\n\\tlist-style-image: none;\\n\\tfont-family: Verdana, Arial, Helvetica, sans-serif;\\n\\tfont-size: 10px;\\n\\tfont-weight: bold;\\n\\tcolor: #FF9900;\\n}\\n.bodytxt2 {\\n\\tfont-family: Verdana, Arial, Helvetica, sans-serif;\\n\\tfont-size: 8pt;\\n\\tfont-weight: normal;\\n\\tcolor: #333333;\\n\\n}\\nA.sac2 {\\n\\tCOLOR: #000000;\\n\\tfont-family: Verdana, Arial, Helvetica, sans-serif;\\n\\tfont-size: 10px;\\n\\tfont-weight: bold;\\n\\ttext-decoration: none;\\n}\\nA.sac2:visited {\\n\\tCOLOR: #314D5A; TEXT-DECORATION: none\\n}\\nA.sac2:hover {\\n\\tCOLOR: #FF9900; TEXT-DECORATION: underline\\n}\\n</style>\\n\\n</head>\\n<script language=JavaScript>\\n\\n\\nvar message=\\\"Function Disabled!\\\";\\n\\n\\nfunction clickIE4(){\\nif (event.button==2){\\nreturn false;\\n}\\n}\\n\\nfunction clickNS4(e){\\nif (document.layers||document.getElementById&&!document.all){\\nif (e.which==2||e.which==3){\\nreturn false;\\n}\\n}\\n}\\n\\nif (document.layers){\\ndocument.captureEvents(Event.MOUSEDOWN);\\ndocument.onmousedown=clickNS4;\\n}\\nelse if (document.all&&!document.getElementById){\\ndocument.onmousedown=clickIE4;\\n}\\n\\ndocument.oncontextmenu=new Function(\\\"return false\\\")\\n\\n</script>\\n<table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">\\n  <tr>\\n    <td align=\\\"left\\\" valign=\\\"top\\\">\\n<table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">\\n        <tr> \\n          <td align=\\\"center\\\" valign=\\\"middle\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">\\n             \\n              <tr>\\n                <td  align=\\\"center\\\"></td>\\n              </tr>\\n              <tr>\\n                <td height=\\\"85\\\" align=\\\"center\\\"><br>\\n                  <table width=\\\"80%\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"1\\\" bgcolor=\\\"#CCCCCC\\\">\\n                    <tr>\\n                      <td bgcolor=\\\"#CCCCCC\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellpadding=\\\"6\\\" cellspacing=\\\"0\\\" bgcolor=\\\"#FFFFFF\\\">\\n                          <tr> \\n                            <td colspan=\\\"2\\\" align=\\\"left\\\" valign=\\\"bottom\\\"><span class=\\\"bodytxt4\\\">Your payment request is being processed...</span></td>\\n                          </tr>\\n                          <tr valign=\\\"top\\\"> \\n                            <td colspan=\\\"2\\\" align=\\\"left\\\"><table width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\">\\n                                <tr> \\n                                  <td width=\\\"87%\\\" bgcolor=\\\"#cccccc\\\" height=\\\"1\\\" align=\\\"center\\\"></td>\\n                                </tr>\\n                              </table></td>\\n                          </tr>\\n                          <tr> \\n                            <td width=\\\"60%\\\" align=\\\"left\\\" valign=\\\"bottom\\\"><table width=\\\"95%\\\" border=\\\"0\\\" cellpadding=\\\"1\\\" cellspacing=\\\"0\\\" bgcolor=\\\"#FFFFFF\\\">\\n                                <tr> \\n                                  <td align=\\\"right\\\" valign=\\\"top\\\"></td>\\n                                  <td class=\\\"bodytxt\\\">&nbsp;</td>\\n                                </tr>\\n                                <tr> \\n                                  <td height=\\\"19\\\"  align=\\\"right\\\" valign=\\\"top\\\"><li class=\\\"bullet1\\\"></li></td>\\n                                  <td class=\\\"bodytxt2\\\">This is a secure payment \\n                                    gateway using 128 bit SSL encryption.</td>\\n                                </tr>\\n                                <tr> \\n                                  <td align=\\\"right\\\" valign=\\\"top\\\"> <li class=\\\"bullet1\\\"></li></td>\\n                                  <td class=\\\"bodytxt2\\\" >When you submit the transaction, \\n                                    the server will take about 1 to 5 seconds \\n                                    to process, but it may take longer at certain \\n                                    times. </td>\\n                                </tr>\\n                                <tr> \\n                                  <td align=\\\"right\\\" valign=\\\"top\\\"><li class=\\\"bullet1\\\"></li></td>\\n                                  <td class=\\\"bodytxt2\\\" >Please do not press \\\"Submit\\\" \\n                                    button once again or the \\\"Back\\\" or \\\"Refresh\\\" \\n                                    buttons. </td>\\n                                </tr>\\n                              </table></td>\\n                            <td align=\\\"right\\\" valign=\\\"bottom\\\"><table width=\\\"80%\\\" border=\\\"0\\\" cellpadding=\\\"1\\\" cellspacing=\\\"0\\\" bgcolor=\\\"#FFFFFF\\\">\\n                                <tr bgcolor=\\\"#FFFCF8\\\"> \\n                                  <td align=\\\"right\\\" bgcolor=\\\"#FFFFFF\\\"></td>\\n                                </tr>\\n                                <tr bgcolor=\\\"#FFFCF8\\\"> \\n                                  <td align=\\\"right\\\" valign=\\\"middle\\\" bgcolor=\\\"#FFFFFF\\\" class=\\\"bodytxt2\\\">&nbsp;</td>\\n                                </tr>\\n                                <tr bgcolor=\\\"#FFFCF8\\\"> \\n                                  <td align=\\\"right\\\" bgcolor=\\\"#FFFFFF\\\" class=\\\"bodytxt2\\\" >&nbsp;</td>\\n                                </tr>\\n                              </table></td>\\n                          </tr>\\n                        </table></td>\\n                    </tr>\\n                  </table>\\n                  \\n                </td>\\n              </tr>\\n            </table>\\n           \\n          \\n         \\n             </td>\\n        </tr>  \\n\\n\\n      </table></td>\\n  </tr>\\n  \\n</table>\\n\\n\\n\\n<body>\\n<form name=\\\"Bankfrm\\\" method=\\\"post\\\" action='https://netbanking.netpnb.com/corp/AuthenticationController?FORMSGROUP_ID__=AuthenticationFG&__START_TRAN_FLAG__=Y&FG_BUTTONS__=LOAD&ACTION.LOAD=Y&AuthenticationFG.LOGIN_FLAG=1&BANK_ID=024&AuthenticationFG.USER_TYPE=1&AuthenticationFG.MENU_ID=CIMSHP&AuthenticationFG.CALL_MODE=2&CATEGORY_ID=400&RU=XotzZqZGcft%2F4sLKouOGEucvgvUmY6s%2BMO5Crj0fOTGGwK4gsC2OmCXUpMHTvzxlX9TYbN3Dr643%0D%0AQy3SuchGAg%3D%3D&QS=%2F5B7Y7PPjoOYiT55Eir1ZFRdDMnTSVq8m4XmlZ6SV2P%2FkHtjs8%2BOg5iJPnkSKvVkQ5vRU5OM5DNr%0D%0Am4L8v%2FrLuu0lzcfEZy2S4Xsq%2BZK4PdvMqF4J5ZyRiufXBpCuILJAXEo%2BavXrP986TJglSNJZ8kCSTLD9%0D%0Ab502zhzeDGZwODd0bczwdf%2FflmsPkoJaChpw6mPPavZJOSOt5KoOjvmo1%2FqwUuij4bWwZSdMQRDgJzmr%0D%0AT9Ih7qR%2BtNHmvx%2BBnW1bYXQKf7woS50ZdPxAGFnU%2Bl2VtiSo5cQsfzt6bV5D38e5aZanhlKZEgs%2FsjQ%2B%0D%0A1aRL'>\\n \\n\\t<input type = \\\"hidden\\\" name = \\\"QS\\\" value=\\\"%2F5B7Y7PPjoOYiT55Eir1ZFRdDMnTSVq8m4XmlZ6SV2P%2FkHtjs8%2BOg5iJPnkSKvVkQ5vRU5OM5DNr%0D%0Am4L8v%2FrLuu0lzcfEZy2S4Xsq%2BZK4PdvMqF4J5ZyRiufXBpCuILJAXEo%2BavXrP986TJglSNJZ8kCSTLD9%0D%0Ab502zhzeDGZwODd0bczwdf%2FflmsPkoJaChpw6mPPavZJOSOt5KoOjvmo1%2FqwUuij4bWwZSdMQRDgJzmr%0D%0AT9Ih7qR%2BtNHmvx%2BBnW1bYXQKf7woS50ZdPxAGFnU%2Bl2VtiSo5cQsfzt6bV5D38e5aZanhlKZEgs%2FsjQ%2B%0D%0A1aRL\\\">\\n\\n\\t</form>\\n</body>\\n<script>\\ndocument.Bankfrm.submit();\\n</script>\\n</html>\\n\"\n" +
                "}"
        val json = JSONObject(data)
        startFragment(NetBankingFragment.newInstance(Bundle().apply {
            putSerializable(NET_BANKING_PAGE, json.optString("data"))
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
            customTabsIntent.launchUrl(requireActivity(), Uri.parse("https://m-investor-onboarding.signzy.tech/icici_prudential2/5d9c3d151d3dce5774055e52/5eaad38f31d9ef1845b86ce6/1588253460/main?ns=icici_Tarrakki"))
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


    private val observerHomeData = Observer<HomeData> {
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
                getPANeKYCStatus(kyc.pan).observe(this, Observer {
                    it?.let { kycStatus ->
                        when {
                            /**
                             * UNDER_PROCESS = "01" & KYC_REGISTERED = "02"
                             * */
                            kycStatus.contains("02") || kycStatus.contains("01") -> {
                                getPANDetails(kyc).observe(this, Observer { data ->
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
                            /**
                             * ON_HOLD = "03"
                             * */
                            kycStatus.contains("03") -> {
                                if (kycStatus.firstOrNull()?.equals("03") == true) {
                                    proceedVideoKYC(kyc)
                                } else {
                                    context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_on_hold))
                                    eventKYCDataLog(kyc, "03")
                                }
                            }
                            /**
                             * KYC_REJECTED = "04"                                     *
                             * */
                            kycStatus.contains("04") -> {
                                if (kycStatus.firstOrNull()?.equals("04") == true) {
                                    proceedVideoKYC(kyc)
                                } else {
                                    context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_rejected))
                                    eventKYCDataLog(kyc, "04")
                                }
                            }
                            kycStatus.contains("99") -> {
                                context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_server_not_reachable))
                                eventKYCDataLog(kyc, "99")
                            }
                            /**
                             * NOT_AVAILABLE = "05"
                             * */
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
                            else -> {
                                context?.simpleAlert(App.INSTANCE.getString(R.string.alert_kyc_server_not_reachable))
                                eventKYCDataLog(kyc, "unknown")
                            }
                        }
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
//            startFragment(ApplyForDebitCartFragment.newInstance(), R.id.frmContainer)
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
