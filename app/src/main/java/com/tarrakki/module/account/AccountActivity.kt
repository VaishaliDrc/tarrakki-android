package com.tarrakki.module.account

import android.content.Intent
import android.os.Bundle
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.fcm.IS_FROM_NOTIFICATION
import com.tarrakki.module.support.SupportFragment
import com.tarrakki.module.support.chat.ChatFragment
import org.supportcompact.ktx.startFragment

class AccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFragment(AccountFragment.newInstance(), R.id.frmContainer)
        if (intent?.getBooleanExtra(IS_FROM_NOTIFICATION, false) == true) {
            openChat()
        }
    }

    override fun onBackPressed() {
        getViewModel().isBackEnabled.value?.let {
            if (it)
                super.onBackPressed()
            else
                finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra(IS_FROM_NOTIFICATION, false) == true) {
            openChat()
        } else {
            supportFragmentManager?.let {
                for (i in 1 until it.backStackEntryCount) {
                    it.popBackStack()
                }
            }
        }
    }

    private fun openChat() {
        val fm = supportFragmentManager?.findFragmentById(R.id.frmContainer)
        if (fm is SupportFragment) {
            startFragment(ChatFragment.newInstance(), R.id.frmContainer)
        } else if (fm !is ChatFragment) {
            startFragment(SupportFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_NOTIFICATION, true) }), R.id.frmContainer)
        }
    }
}
