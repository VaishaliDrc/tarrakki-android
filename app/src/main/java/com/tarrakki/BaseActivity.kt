package com.tarrakki

import android.arch.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.view.MenuItem
import com.tarrakki.databinding.ActivityBaseBinding
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.invest.InvestActivity
import com.tarrakki.module.plan.PlanActivity
import kotlinx.android.synthetic.main.activity_base.*
import org.supportcompact.ActivityViewModel
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.startActivity
import org.supportcompact.widgets.BottomNavigationViewHelper

abstract class BaseActivity : CoreActivity<ActivityViewModel, ActivityBaseBinding>() {

    protected val ACTION_FINISH_ALL_TASK = "ACTION_FINISH_ALL_TASK"

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
                mToolBar?.setNavigationIcon(if (it) R.drawable.ic_arrow_back_white_24dp else R.drawable.ic_menu_white_24dp)
            }
        })
        setToolBar()
        BottomNavigationViewHelper.disableShiftMode(mBottomNav)
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
                    true
                }
                R.id.action_info -> {
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                getViewModel().isBackEnabled.value?.let {
                    if (it) {
                        onBackPressed()
                    } else {
                        //TODO("Open Drawer")
                    }
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
}