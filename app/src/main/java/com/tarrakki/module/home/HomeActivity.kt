package com.tarrakki.module.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tarrakki.*
import com.tarrakki.fcm.ACTION_CLOSE_KYC_PORTAL
import com.tarrakki.fcm.IS_FROM_NOTIFICATION
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.ekyc.EKYCRemainingDetailsFragment
import com.tarrakki.module.ekyc.IS_FROM_VIDEO_KYC
import com.tarrakki.module.ekyc.KYCData
import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.json.JSONObject
import org.supportcompact.events.Event
import org.supportcompact.events.EventData
import org.supportcompact.ktx.*


class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendDeviceDetails()
        startFragment(HomeFragment.newInstance(), R.id.frmContainer)
    }

    override fun onBackPressed() {
        getViewModel().isBackEnabled.value?.let {
            if (it)
                super.onBackPressed()
            else
                confirmationDialog(getString(R.string.are_you_sure_you_want_to_exit), btnPositiveClick = {
                    LocalBroadcastManager.getInstance(this@HomeActivity).sendBroadcast(Intent(ACTION_FINISH_ALL_TASK))
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            it?.let {
                tvCartCount?.cartCount(it)
            }
        })
        menu?.findItem(R.id.itemHome)?.actionView?.setOnClickListener {
            startFragment(CartFragment.newInstance(), R.id.frmContainer)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        try {
            val branch = Branch.getInstance(applicationContext)
            branch.initSession(branchReferralInitListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra(IS_FROM_NOTIFICATION, false) == true && intent.hasExtra(ACTION_CLOSE_KYC_PORTAL)) {
            resumeKYCProcess(intent)
        } else {
            supportFragmentManager?.let {
                for (i in 1 until it.backStackEntryCount) {
                    it.popBackStack()
                }
            }
        }
        this.intent = intent
    }

    private fun resumeKYCProcess(intent: Intent) {
        when {
            App.INSTANCE.getRemainingFields().toIntOrNull() == 1 -> {
                startFragment(BankAccountsFragment.newInstance(Bundle().apply {
                    putBoolean(IS_FROM_VIDEO_KYC, true)
                    putBoolean(IS_FROM_COMLETE_REGISTRATION, true)
                }), R.id.frmContainer)
            }
            App.INSTANCE.getRemainingFields().toIntOrNull() == 2 -> {
                startFragment(EKYCRemainingDetailsFragment.newInstance(), R.id.frmContainer)
            }
            else -> {
                startFragment(BankAccountsFragment.newInstance(Bundle().apply {
                    putBoolean(IS_FROM_VIDEO_KYC, true)
                    putBoolean(IS_FROM_COMLETE_REGISTRATION, true)
                }), R.id.frmContainer)
            }
        }
        val kycData: KYCData? = intent.getSerializableExtra(ACTION_CLOSE_KYC_PORTAL) as KYCData?
        kycData?.let { postSticky(it) }
    }

    private val branchReferralInitListener = object : Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
            if (error == null) {
                referringParams?.let { e("BRANCH SDK", it) }
                if (referringParams?.optString("~referring_link")?.contains("tarrakki_zyaada") == true) {
                    //EventBus.getDefault().post(Event.OPEN_TARRAKKI_ZYAADA)
                    postSticky(Event.OPEN_TARRAKKI_ZYAADA)
                } else if (referringParams?.optString("~referring_link")?.contains("investment_strategy?id=") == true) {
                    try {
                        val uri = Uri.parse(referringParams.optString("~referring_link"))
                        uri?.let {
                            //EventBus.getDefault().post(EventData(Event.OPEN_MY_CART, "${uri.getQueryParameter("id")}"))
                            postSticky(EventData(Event.OPEN_MY_CART, "${uri.getQueryParameter("id")}"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                /*if(referringParams?.optBoolean("+clicked_branch_link") == true){
                }*/
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                e("BRANCH SDK", error.message)
            }
        }

    }

}