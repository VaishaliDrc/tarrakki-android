package com.tarrakki.module.account

import android.content.Intent
import android.os.Bundle
import com.tarrakki.BaseActivity
import com.tarrakki.OPEN_BANK_MANDATE
import com.tarrakki.R
import com.tarrakki.api.model.SupportViewTicketResponse
import com.tarrakki.fcm.IS_BANK_ACCOUNT
import com.tarrakki.fcm.IS_FROM_NOTIFICATION
import com.tarrakki.fcm.IS_VIDEO_KYC
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.bankmandate.BankMandateFragment
import com.tarrakki.module.support.SupportFragment
import com.tarrakki.module.support.chat.ChatFragment
import org.greenrobot.eventbus.EventBus
import org.supportcompact.events.Event
import org.supportcompact.ktx.startFragment


class AccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFragment(AccountFragment.newInstance(), R.id.frmContainer)
        if (intent?.getBooleanExtra(IS_FROM_NOTIFICATION, false) == true) {
            when {
                intent.hasExtra(IS_VIDEO_KYC) -> {
                    EventBus.getDefault().post(Event.REFRESH)
                }
                intent.hasExtra(IS_BANK_ACCOUNT) -> {
                    openBank()
                }
                else -> {
                    openChat(intent)
                }
            }
        } else if (intent?.getBooleanExtra(OPEN_BANK_MANDATE, false) == true) {
            openBankMandate()
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
            when {
                intent.hasExtra(IS_VIDEO_KYC) -> {
                    EventBus.getDefault().post(Event.REFRESH)
                }
                intent.hasExtra(IS_BANK_ACCOUNT) -> {
                    openBank()
                }
                else -> {
                    openChat(intent)
                }
            }
        } else if (intent?.getBooleanExtra(OPEN_BANK_MANDATE, false) == true) {
            openBankMandate()
        } else {
            supportFragmentManager?.let {
                for (i in 1 until it.backStackEntryCount) {
                    it.popBackStack()
                }
            }
        }
    }

    private fun openBank() {
        val fm = supportFragmentManager?.findFragmentById(R.id.frmContainer)
        if (fm is BankAccountsFragment) {
            post(Event.REFRESH_ACCOUNT)
        } else {
            startFragment(BankAccountsFragment.newInstance(), R.id.frmContainer)
        }
    }

    private fun openBankMandate() {
        val fm = supportFragmentManager?.findFragmentById(R.id.frmContainer)
        if (fm is BankMandateFragment) {
            post(Event.REFRESH_ACCOUNT)
        } else {
            startFragment(BankMandateFragment.newInstance(), R.id.frmContainer)
        }
    }

    private fun openChat(intent: Intent?) {
        val fm = supportFragmentManager?.findFragmentById(R.id.frmContainer)
        if (fm is SupportFragment || fm is ChatFragment) {
            startFragment(ChatFragment.newInstance(), R.id.frmContainer)
        } else {
            startFragment(SupportFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_NOTIFICATION, true) }), R.id.frmContainer)
        }
        intent?.getStringExtra("reference")?.let { ticketId ->
            val tiket = SupportViewTicketResponse.Data.Conversation(
                    null,
                    "open",
                    ticketId,
                    null,
                    null
            )
            EventBus.getDefault().postSticky(tiket)
        }
    }
}
