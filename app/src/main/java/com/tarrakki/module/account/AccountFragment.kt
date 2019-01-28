package com.tarrakki.module.account


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.tarrakki.ACTION_FINISH_ALL_TASK
import com.tarrakki.App
import com.tarrakki.IS_FROM_ACCOUNT
import com.tarrakki.R
import com.tarrakki.databinding.FragmentAccountBinding
import com.tarrakki.databinding.RowAccountMenuItemBinding
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.changepassword.ChangePasswordFragment
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.myprofile.ProfileFragment
import com.tarrakki.module.portfolio.PortfolioFragment
import com.tarrakki.module.savedgoals.SavedGoalsFragment
import com.tarrakki.module.transactions.TransactionsFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_account.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.clearUserData
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.setIsLogin
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
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
                            putBoolean("isResetPassword",false)
                        }
                        startFragment(ChangePasswordFragment.newInstance(bundle), R.id.frmContainer)
                    }
                    R.drawable.ic_my_profile -> {
                        if (App.INSTANCE.isLoggedIn.value!!) {
                            //Open My Profile
                            startFragment(ProfileFragment.newInstance(), R.id.frmContainer)
                        } else {
                            //Open Redirect to login screen
                            startActivity(Intent(activity, LoginActivity::class.java).apply {
                                putExtra(IS_FROM_ACCOUNT, true)
                            })
                        }
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
        btnLogout?.setOnClickListener {
            context?.confirmationDialog(getString(R.string.are_you_sure_you_want_logout), btnPositiveClick = {
                //App.INSTANCE.isLoggedIn.value = false
                it.context.clearUserData()
                startActivity(Intent(it.context, LoginActivity::class.java))
                LocalBroadcastManager.getInstance(it.context).sendBroadcast(Intent(ACTION_FINISH_ALL_TASK))
            })
        }
        App.INSTANCE.isLoggedIn.observe(this, Observer { isLogin ->
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
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AccountFragment().apply { arguments = basket }
    }
}
