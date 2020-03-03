package com.tarrakki

import android.app.KeyguardManager
import androidx.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import com.tarrakki.databinding.ActivityBaseBinding
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.bankmandate.BankMandateFormFragment
import com.tarrakki.module.bankmandate.BankMandateSuccessFragment
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.learn.LearnActivity
import com.tarrakki.module.netbanking.NetBankingFragment
import com.tarrakki.module.paymentmode.PaymentModeFragment
import com.tarrakki.module.plan.PlanActivity
import com.tarrakki.module.redeem.RedemptionStatusFragment
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import com.tarrakki.module.transactions.TransactionsFragment
import kotlinx.android.synthetic.main.activity_base.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.ActivityViewModel
import org.supportcompact.CoreActivity
import org.supportcompact.events.ShowECutOffTimeDialog
import org.supportcompact.inputclasses.keyboardListener
import org.supportcompact.ktx.*

const val ACTION_FINISH_ALL_TASK = "ACTION_FINISH_ALL_TASK"

abstract class BaseActivity : CoreActivity<ActivityViewModel, ActivityBaseBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_base
    }

    override fun createViewModel(): Class<out ActivityViewModel> {
        return ActivityViewModel::class.java
    }

    override fun setVM(binding: ActivityBaseBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).registerReceiver(finisAllTask, IntentFilter(ACTION_FINISH_ALL_TASK))
        getViewModel().isBackEnabled.observe(this, Observer { it ->
            it?.let {
                mToolBar?.setNavigationIcon(if (it) getDrawable(R.drawable.ic_arrow_back_white_24dp) else null/*R.drawable.ic_menu_white_24dp*/)
            }
        })

        getViewModel().isEmpty.observe(this, Observer {
            if (it!!) {
                txt_empty.visibility = View.VISIBLE
            } else {
                txt_empty.visibility = View.GONE
            }
        })

        setToolBar()
        //BottomNavigationViewHelper.disableShiftMode(mBottomNav)
        mBottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_home -> {
                    if (this@BaseActivity !is HomeActivity) {
                        startActivity<HomeActivity>()
                    }
                    true
                }
                R.id.action_plan -> {
                    if (this@BaseActivity !is PlanActivity) {
                        startActivity<PlanActivity>()
                    }
                    true
                }
                R.id.action_invest -> {
                    if (this@BaseActivity !is InvestActivity) {
                        startActivity<InvestActivity>()
                    }
                    true
                }
                R.id.action_learn -> {
                    if (this@BaseActivity !is LearnActivity) {
                        startActivity<LearnActivity>()
                    }
                    true
                }
                R.id.action_account -> {
                    if (this@BaseActivity !is AccountActivity) {
                        startActivity<AccountActivity>()
                    }
                    true
                }
                else -> {
                    true
                }
            }
        }
        keyboardListener { isOpen ->
            getViewModel().footerVisibility.set(if (isOpen) View.GONE else View.VISIBLE)
        }

        App.INSTANCE.isAuthorise.observe(this, Observer {
            it?.let { it_ ->
                if (hasAppLock() && !it_) {
                    isAuthorise()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                getViewModel().isBackEnabled.value?.let {
                    val fragment = supportFragmentManager?.findFragmentById(R.id.frmContainer)
                    if (fragment is CartFragment ||
                            fragment is BankMandateSuccessFragment ||
                            fragment is TransactionsFragment ||
                            fragment is PaymentModeFragment ||
                            fragment is TransactionConfirmFragment ||
                            fragment is BankMandateFormFragment ||
                            fragment is RedemptionStatusFragment ||
                            fragment is NetBankingFragment) {
                        return super.onOptionsItemSelected(item)
                    } else {
                        onBackPressed()
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showDialog(show: String) {
        when (show) {
            DISMISS_PROGRESS -> {
                App.INSTANCE.isRefreshing.value = false
            }
            ONLOGOUT -> {
                onLogout()
            }
        }
        super.showDialog(show)
    }

    @Subscribe
    fun showError(error: ShowECutOffTimeDialog) {
        showCutOffTimeDialog(error)
    }

    /**
     * setting the toolbar
     */
    private fun setToolBar() {
        setSupportActionBar(mToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mToolBar?.setNavigationIcon(R.drawable.ic_menu_white_24dp)
    }

    override fun onResume() {
        super.onResume()
        when (this@BaseActivity) {
            is HomeActivity -> mBottomNav.selectedItemId = R.id.action_home
            is PlanActivity -> mBottomNav.selectedItemId = R.id.action_plan
            is InvestActivity -> mBottomNav.selectedItemId = R.id.action_invest
            is LearnActivity -> mBottomNav.selectedItemId = R.id.action_learn
            is AccountActivity -> mBottomNav.selectedItemId = R.id.action_account
        }
    }

    private val finisAllTask = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).unregisterReceiver(finisAllTask)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                App.INSTANCE.isAuthorise.value = true
                //do something you want when pass the security
            } else {
                finish()
            }
        }
    }

    @RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
    private fun isAuthorise() {
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val authIntent = km.createConfirmDeviceCredentialIntent(null, null)
            startActivityForResult(authIntent, INTENT_AUTHENTICATE)
        }
    }

    companion object {
        private const val INTENT_AUTHENTICATE: Int = 111
    }
}