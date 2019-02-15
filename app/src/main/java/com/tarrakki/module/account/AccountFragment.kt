package com.tarrakki.module.account


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.tarrakki.*
import com.tarrakki.databinding.FragmentAccountBinding
import com.tarrakki.databinding.RowAccountMenuItemBinding
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.changepassword.ChangePasswordFragment
import com.tarrakki.module.ekyc.KYCData
import com.tarrakki.module.ekyc.KYCRegistrationAFragment
import com.tarrakki.module.ekyc.isPANCard
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.myprofile.ProfileFragment
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
                        startFragment(ProfileFragment.newInstance(), R.id.frmContainer)
                        /*if (App.INSTANCE.isLoggedIn.value!!) {
                            //Open My Profile
                            startFragment(ProfileFragment.newInstance(), R.id.frmContainer)
                        } else {
                            //Open Redirect to login screen
                            startActivity(Intent(activity, LoginActivity::class.java).apply {
                                putExtra(IS_FROM_ACCOUNT, true)
                            })
                        }*/
                    }
                    R.drawable.ic_my_portfolio -> {
                        startFragment(PortfolioFragment.newInstance(), R.id.frmContainer)
                    }
                    R.drawable.ic_saved_goals -> {
                        startFragment(SavedGoalsFragment.newInstance(), R.id.frmContainer)
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
                }
            }
        }
        tvBankAccount?.setOnClickListener {
            startFragment(BankAccountsFragment.newInstance(), R.id.frmContainer)
        }
        tvBankMandateAccount?.setOnClickListener {
            startFragment(BankMandateFragment.newInstance(), R.id.frmContainer)
            //context?.simpleAlert("Under Construction.")
        }
        btnLogout?.setOnClickListener {
            context?.confirmationDialog(getString(R.string.are_you_sure_you_want_logout), btnPositiveClick = {
                //App.INSTANCE.isLoggedIn.value = false
                it.context.clearUserData()
                startActivity(Intent(it.context, LoginActivity::class.java))
                LocalBroadcastManager.getInstance(it.context).sendBroadcast(Intent(ACTION_FINISH_ALL_TASK))
            })
        }
        edtPanNo?.applyPAN()
        tvNext?.setOnClickListener {
            if (edtPanNo.length() == 0) {
                //startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                context?.simpleAlert("Please enter PAN card number")
            } else if (!isPANCard(edtPanNo.text.toString())) {
                context?.simpleAlert("Please enter valid PAN card number")
            } else {
                it.dismissKeyboard()
                val kyc = KYCData(edtPanNo.text.toString(), "${App.INSTANCE.getEmail()}", "${App.INSTANCE.getMobile()}")
                /*getEncryptedPasswordForCAMPSApi().observe(this, android.arch.lifecycle.Observer {
                    it?.let { password ->
                        getPANeKYCStatus(password, kyc.pan).observe(this, android.arch.lifecycle.Observer {
                            getEKYCData(password, kyc.pan).observe(this, Observer {
                                startFragment(KYCRegistrationAFragment.newInstance(), R.id.frmContainer)
                                postSticky(kyc)
                            })
                        })
                    }
                })*/
                startFragment(BankAccountsFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_COMLETE_REGISTRATION, true) }), R.id.frmContainer)
                post(kyc)
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
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AccountFragment().apply { arguments = basket }
    }
}
