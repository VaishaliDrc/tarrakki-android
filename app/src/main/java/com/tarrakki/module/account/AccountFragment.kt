package com.tarrakki.module.account


import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ShareCompat
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import com.tarrakki.*
import com.tarrakki.databinding.FragmentAccountBinding
import com.tarrakki.databinding.RowAccountMenuItemBinding
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.changepassword.ChangePasswordFragment
import com.tarrakki.module.debitcart.DebitCartInfoFragment
import com.tarrakki.module.ekyc.*
import com.tarrakki.module.my_sip.MySipFragment
import com.tarrakki.module.myprofile.MyProfileFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.risk_profile.RiskProfileFragment
import com.tarrakki.module.risk_profile.StartAssessmentFragment
import com.tarrakki.module.savedgoals.SavedGoalsFragment
import com.tarrakki.module.support.SupportFragment
import com.tarrakki.module.transactions.TransactionsFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_account.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import java.util.*


class AccountFragment : CoreFragment<AccountVM, FragmentAccountBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.my_account)

    override fun getLayout(): Int {
        return R.layout.fragment_account
    }

    override fun createViewModel(): Class<out AccountVM> {
        return AccountVM::class.java
    }

    override fun setVM(binding: FragmentAccountBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        if (getViewModel().needToCheckStatus) {
            refreshKYCStatus()
            getViewModel().needToCheckStatus = false
        }
        getViewModel().setAccountMenu()
        rvMenus?.adapter?.notifyDataSetChanged()
        if (getViewModel().isAppLockClick && getViewModel().appLock.get() == false) {
            val km = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            getViewModel().appLock.set(km.isKeyguardSecure)
            getViewModel().isAppLockClick = false
        } else if (App.INSTANCE.hasAppLock()) {
            val km = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            getViewModel().appLock.set(km.isKeyguardSecure)
        }
    }

    private fun refreshKYCStatus() {
        getViewModel().getKYCStatus().observe(this, Observer {
            if (context?.getKYCStatus()?.isEmpty() == true) {
                getViewModel().docStatus.clear()
                getViewModel().docStatus.add(getViewModel().readyToInvest)
                /**
                 * Normal flow of KYC and complete registration
                 * */
                ll_complete_verification?.visibility = if (context?.isCompletedRegistration() == true) View.GONE else View.VISIBLE
                getViewModel().bankVisibility.set(if (context?.isCompletedRegistration() == true) View.VISIBLE else View.GONE)
                getViewModel().bankMandateVisibility.set(if (context?.isCompletedRegistration() == true) View.VISIBLE else View.GONE)
                getViewModel().btnComleteRegion.set(context?.isKYCVerified() == true)
                rvDocStatus?.visibility = if (ll_complete_verification?.visibility == View.GONE && context?.isReadyToInvest() == false) View.VISIBLE else View.GONE
                rvDocStatus.adapter?.notifyDataSetChanged()
            } else {
                /**
                 * Set status as per kyc status
                 * */
                ll_complete_verification?.visibility = View.GONE
                rvDocStatus?.visibility = View.VISIBLE
                if (App.INSTANCE.getRemainingFields().toIntOrNull() == 0 || App.INSTANCE.getRemainingFields().toIntOrNull() == 2) {
                    getViewModel().bankVisibility.set(View.VISIBLE)
                    getViewModel().bankMandateVisibility.set(View.GONE)
                } else {
                    getViewModel().bankVisibility.set(View.GONE)
                    getViewModel().bankMandateVisibility.set(View.GONE)
                }
                setViewAsKYCStatus("${context?.getKYCStatus()}".toUpperCase(Locale.US))
            }
        })
    }

    private fun setViewAsKYCStatus(status: String) {
        getViewModel().docStatus.clear()
        when (status) {
            "INCOMPLETE" -> {
                getViewModel().docStatus.add(VideoKYCStatus(KYC_STATUS_INCOMPLETE))
            }
            "UNDERPROCESS" -> {
                getViewModel().docStatus.add(VideoKYCStatus(KYC_STATUS_UNDER_PROCESS))
            }
            "ACCEPTED" -> {
                getViewModel().docStatus.add(VideoKYCStatus(KYC_STATUS_APPROVED))
            }
            "REJECTED" -> {
                getViewModel().docStatus.add(VideoKYCStatus(KYC_STATUS_REJECTED, App.INSTANCE.getRemark()))
            }
            "REDO" -> {
                getViewModel().docStatus.add(VideoKYCStatus(KYC_STATUS_INCOMPLETE))
            }
            "KRA-ACCEPTED" -> {
                ll_complete_verification?.visibility = View.VISIBLE
                rvDocStatus?.visibility = View.GONE
            }
        }
        rvDocStatus?.adapter?.notifyDataSetChanged()
    }

    override fun createReference() {

        if (context?.getKYCStatus()?.isEmpty() == true) {
            /**
             * Normal flow of KYC and complete registration
             * */
            ll_complete_verification?.visibility = if (context?.isCompletedRegistration() == true) View.GONE else View.VISIBLE
            getViewModel().bankVisibility.set(if (context?.isCompletedRegistration() == true) View.VISIBLE else View.GONE)
            getViewModel().bankMandateVisibility.set(if (context?.isCompletedRegistration() == true) View.VISIBLE else View.GONE)
            getViewModel().btnComleteRegion.set(context?.isKYCVerified() == true)
            rvDocStatus?.visibility = if (ll_complete_verification?.visibility == View.GONE && context?.isReadyToInvest() == false) View.VISIBLE else View.GONE
        } else {
            /**
             * Set status as per kyc statusl
             * */
            ll_complete_verification?.visibility = View.GONE
            rvDocStatus?.visibility = View.VISIBLE
            if (App.INSTANCE.getRemainingFields().toIntOrNull() == 0 || App.INSTANCE.getRemainingFields().toIntOrNull() == 2) {
                getViewModel().bankVisibility.set(View.VISIBLE)
                getViewModel().bankMandateVisibility.set(View.GONE)
            } else {
                getViewModel().bankVisibility.set(View.GONE)
                getViewModel().bankMandateVisibility.set(View.GONE)
            }
            setViewAsKYCStatus("${context?.getKYCStatus()}".toUpperCase(Locale.US))
        }

        rvDocStatus?.setUpMultiViewRecyclerAdapter(getViewModel().docStatus) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onComplete, View.OnClickListener {
                if (item is VideoKYCStatus) {
                    getViewModel().needToCheckStatus = true
                    val kyc = KYCData(edtPanNo.text.toString(), "${App.INSTANCE.getEmail()}", "${App.INSTANCE.getMobile()}")
                    when {
                        (item.status == KYC_STATUS_UNDER_PROCESS || item.status == KYC_STATUS_REJECTED) && App.INSTANCE.getRemainingFields().toIntOrNull() == 1 -> {
                            startFragment(BankAccountsFragment.newInstance(Bundle().apply {
                                putBoolean(IS_FROM_VIDEO_KYC, true)
                                putBoolean(IS_FROM_COMLETE_REGISTRATION, true)
                            }), R.id.frmContainer)
                            postSticky(kyc)
                        }
                        (item.status == KYC_STATUS_UNDER_PROCESS || item.status == KYC_STATUS_REJECTED) && App.INSTANCE.getRemainingFields().toIntOrNull() == 2 -> {
                            startFragment(EKYCRemainingDetailsFragment.newInstance(), R.id.frmContainer)
                            postSticky(kyc)
                        }
                        else -> {
                            /*apiApplyForNewKYC().observe(this, Observer {
                                it?.let {
                                    kyc.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                                    startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                                    postSticky(kyc)
                                }
                            })*/
                            startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
                            postSticky(kyc)
                        }
                    }
                }

            })
            binder.setVariable(BR.onVerifyYourPan, View.OnClickListener {
                ll_complete_verification?.visibility = View.VISIBLE
                rvDocStatus?.visibility = View.GONE
            })
            binder.executePendingBindings()
        }

        rvMenus?.setUpRecyclerView(R.layout.row_account_menu_item, getViewModel().accountMenus) { item: AccountMenu, binder: RowAccountMenuItemBinding, position ->
            binder.menu = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                when (item.imgRes) {
                    R.drawable.ic_change_password -> {
                        val bundle = Bundle().apply {
                            putBoolean("isResetPassword", false)
                        }
                        startFragment(ChangePasswordFragment.newInstance(bundle), R.id.frmContainer)
                    }
                    R.drawable.ic_my_profile -> {
                        startFragment(MyProfileFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_my_portfolio -> {
                        startFragment(PortfolioFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_my_sip -> {
                        startFragment(MySipFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_saved_goals -> {
                        if (position == 5) {
                            getReportOfRiskProfile().observe(this, Observer { apiRes ->
                                if (apiRes.status?.code == 1) {
                                    startFragment(RiskProfileFragment.newInstance(), R.id.frmContainer)
                                    postSticky(apiRes)
                                } else {
                                    startFragment(StartAssessmentFragment.newInstance(), R.id.frmContainer)
                                }
                            })
                        } else {
                            startFragment(SavedGoalsFragment.newInstance(), R.id.frmContainer)
                        }
                    }
                    R.drawable.ic_transactions -> {
                        startFragment(TransactionsFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_privacy_policy -> {
                        startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                        postSticky(Event.PRIVACY_PAGE)
                    }
                    R.drawable.ic_terms_conditions -> {
                        startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                        postSticky(Event.TERMS_AND_CONDITIONS_PAGE)
                    }
                    R.drawable.ic_debit_cart -> {
                        startFragment(DebitCartInfoFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_support -> {
                        //context?.simpleAlert(getString(R.string.coming_soon))
                        startFragment(SupportFragment.newInstance(), R.id.frmContainer)
                    }
                }
            }
        }
        tvBankAccount?.setOnClickListener {
            startFragment(BankAccountsFragment.newInstance(), R.id.frmContainer)
        }
        tvBankMandateAccount?.setOnClickListener {
            startFragment(BankMandateFragment.newInstance(), R.id.frmContainer)
        }
        tvInvite?.setOnClickListener {
            ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share link")
                    .setText(App.INSTANCE.getString(R.string.invite_your_friend)
                            .plus("\n\n")
                            .plus("http://play.google.com/store/apps/details?id=${activity?.packageName}")
                            .plus("\n\n")
                            .plus("https://itunes.apple.com/in/app/Tarrakki/id1459230748?mt=8"))
                    .startChooser()
            /*.setText(App.INSTANCE.getString(R.string.invite_your_friend)
                    .plus("\n\n")
                    .plus("http://play.google.com/store/apps/details?id=" + activity?.packageName))
            .startChooser()*/
        }
        btnLogout?.setOnClickListener {
            context?.let { context ->
                context.confirmationDialog(getString(R.string.are_you_sure_you_want_logout), btnPositiveClick = {
                    getViewModel().doLogout().observe(this, Observer {
                        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        if (checkSelfPermissions(permissions)) {
                            val mFile = getTarrakkiDir()
                            mFile.deleteRecursively()
                        }
                        context.onLogout()
                    })
                })
            }
        }
        //edtPanNo?.applyPAN()
        btnContinue?.setOnClickListener {
            getKYCData().observe(this, Observer {
                it?.let { kycData ->
                    when (kycData.pageNo) {
                        2 -> {
                            startFragment(KYCRegistrationBFragment.newInstance(), R.id.frmContainer)
                        }
                        3 -> {
                            startFragment(BankAccountsFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_COMLETE_REGISTRATION, true) }), R.id.frmContainer)
                        }
                        else -> {
                            startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                        }
                    }
                    postSticky(kycData)
                    getViewModel().needToCheckStatus = true
                }
            })
        }
        tvNext?.setOnClickListener {
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
                                                getViewModel().needToCheckStatus = true
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

        switchOnOff?.setOnClickListener {
            getViewModel().isAppLockClick = true
            val km = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (!km.isKeyguardSecure) {
                getViewModel().appLock.set(false)
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                startActivity(intent)
            } else {
                getViewModel().appLock.set(getViewModel().appLock.get() != true)
            }
        }
    }

    private fun proceedVideoKYC(kyc: KYCData) {
        edtPanNo?.text?.clear()
        getViewModel().needToCheckStatus = true
        startFragment(EKYCConfirmationFragment.newInstance(), R.id.frmContainer)
        postSticky(kyc)
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)
        if (event == Event.REFRESH) {
            //rvDocStatus?.visibility = if (ll_complete_verification?.visibility == View.GONE && context?.isReadyToInvest() == false) View.VISIBLE else View.GONE
            /*if (context?.getKYCStatus()?.isEmpty() == true) {
                */
            /**
             * Normal flow of KYC and complete registration
             * *//*
                ll_complete_verification?.visibility = if (context?.isCompletedRegistration() == true) View.GONE else View.VISIBLE
                getViewModel().bankVisibility.set(if (context?.isCompletedRegistration() == true) View.VISIBLE else View.GONE)
                getViewModel().btnComleteRegion.set(context?.isKYCVerified() == true)
                rvDocStatus?.visibility = if (ll_complete_verification?.visibility == View.GONE && context?.isReadyToInvest() == false) View.VISIBLE else View.GONE
            } else {
                */
            /**
             * Set status as per kyc status
             * *//*
                ll_complete_verification?.visibility = View.GONE
                rvDocStatus?.visibility = View.VISIBLE
                setViewAsKYCStatus("${context?.getKYCStatus()}".toUpperCase(Locale.US))
            }*/
            activity?.runOnUiThread {
                refreshKYCStatus()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AccountFragment().apply { arguments = basket }
    }
}
