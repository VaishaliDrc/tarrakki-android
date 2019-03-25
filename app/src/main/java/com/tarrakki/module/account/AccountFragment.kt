package com.tarrakki.module.account


import android.app.KeyguardManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ShareCompat
import android.view.View
import com.tarrakki.*
import com.tarrakki.databinding.FragmentAccountBinding
import com.tarrakki.databinding.RowAccountMenuItemBinding
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.changepassword.ChangePasswordFragment
import com.tarrakki.module.ekyc.*
import com.tarrakki.module.myprofile.MyProfileFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.savedgoals.SavedGoalsFragment
import com.tarrakki.module.transactions.TransactionsFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_account.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.events.Event
import org.supportcompact.ktx.*


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
        ll_complete_verification?.visibility = if (context?.isCompletedRegistration() == true) View.GONE else View.VISIBLE
        getViewModel().btnComleteRegion.set(context?.isKYCVerified() == true)
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

    override fun createReference() {

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
                        /*startFragment(BankAccountsFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_COMLETE_REGISTRATION, true) }), R.id.frmContainer)
                        val kyc = KYCData(edtPanNo.text.toString(), "${App.INSTANCE.getEmail()}", "${App.INSTANCE.getMobile()}")
                        postSticky(kyc)*/
                    }
                    R.drawable.ic_my_portfolio -> {
                        //context?.simpleAlert("Portfolio is still under development so you will be able to test it in the next build.")

                        startFragment(PortfolioFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_saved_goals -> {
                        startFragment(SavedGoalsFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_transactions -> {
                        //context?.simpleAlert("Transactions is still under development so you will be able to test it in the next build.")
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
                }
            }
        }
        tvBankAccount?.setOnClickListener {
            startFragment(BankAccountsFragment.newInstance(), R.id.frmContainer)
        }
        tvBankMandateAccount?.setOnClickListener {
            startFragment(BankMandateFragment.newInstance(), R.id.frmContainer)
            // context?.simpleAlert(getString(R.string.coming_soon))
        }
        tvInvite?.setOnClickListener {
            ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share link")
                    .setText("http://play.google.com/store/apps/details?id=" + activity?.packageName)
                    .startChooser()
        }
        btnLogout?.setOnClickListener {
            context?.let { context ->
                context.confirmationDialog(getString(R.string.are_you_sure_you_want_logout), btnPositiveClick = {
                    getViewModel().doLogout().observe(this, Observer {
                        context.onLogout()
                    })
                })
            }
        }
        edtPanNo?.applyPAN()
        btnContinue?.setOnClickListener {
            getKYCData().observe(this, android.arch.lifecycle.Observer {
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
                }
            })
        }
        tvNext?.setOnClickListener {
            /*context?.simpleAlert(getString(R.string.coming_soon))
            return@setOnClickListener*/
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
                /*startFragment(BankAccountsFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_COMLETE_REGISTRATION, true) }), R.id.frmContainer)
                post(kyc)*/
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
                /*kyc.mobile = "8460421008"
                startFragment(EKYCWebViewFragment.newInstance(), R.id.frmContainer)
                postSticky(kyc)*/
            }
        }
        /*App.INSTANCE.isLoggedIn.observe(this, Observer { isLogin ->
            isLogin?.let {
                if (it) {
                    getViewModel().logoutVisibility.set(View.VISIBLE)
                    getViewModel().setAccountMenu()
                } else {
                    context?.setIsLogin(it)
                    getViewModel().logoutVisibility.set(View.GONE)
                    getViewModel().setAccountMenu()
                }
                rvMenus?.adapter?.notifyDataSetChanged()
            }
        })*/

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

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AccountFragment().apply { arguments = basket }
    }
}
