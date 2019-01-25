package com.tarrakki

import android.app.KeyguardManager
import android.arch.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.annotation.RequiresApi
import android.support.v4.content.LocalBroadcastManager
import android.view.MenuItem
import android.view.View
import com.tarrakki.databinding.ActivityBaseBinding
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.learn.LearnActivity
import com.tarrakki.module.plan.PlanActivity
import kotlinx.android.synthetic.main.activity_base.*
import org.supportcompact.ActivityViewModel
import org.supportcompact.CoreActivity
import org.supportcompact.events.Event
import org.supportcompact.inputclasses.keyboardListener
import org.supportcompact.ktx.hasAppLock
import org.supportcompact.ktx.startActivity

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
        LocalBroadcastManager.getInstance(this).registerReceiver(finisAllTask, IntentFilter(ACTION_FINISH_ALL_TASK))
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
                    val f = supportFragmentManager.findFragmentByTag(CartFragment::class.java.name)
                    if (f !is CartFragment)
                        onBackPressed()
                    else
                        return super.onOptionsItemSelected(item)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
        when {
            this@BaseActivity is HomeActivity -> mBottomNav.selectedItemId = R.id.action_home
            this@BaseActivity is PlanActivity -> mBottomNav.selectedItemId = R.id.action_plan
            this@BaseActivity is InvestActivity -> mBottomNav.selectedItemId = R.id.action_invest
            this@BaseActivity is LearnActivity -> mBottomNav.selectedItemId = R.id.action_learn
            this@BaseActivity is AccountActivity -> mBottomNav.selectedItemId = R.id.action_account
        }
    }

    private val finisAllTask = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finishAndRemoveTask()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finisAllTask)
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