package com.tarrakki.module.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tarrakki.*
import com.tarrakki.module.cart.CartFragment
import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.events.Event
import org.supportcompact.ktx.cartCount
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.e
import org.supportcompact.ktx.startFragment


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
            Branch.getInstance().initSession(BranchListener, intent.data, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager?.let {
            for (i in 1 until it.backStackEntryCount) {
                it.popBackStack()
            }
        }
        //Branch reinit ( in case Activity is already in foreground when Branch link is clicked)
        Branch.getInstance().reInitSession(this, BranchListener)
    }

    object BranchListener : Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
            if (error == null) {
                referringParams?.let { e("BRANCH SDK", it) }
                if (referringParams?.optString("\$canonical_url")?.contains("tarrakki_zyaada") == true) {
                    EventBus.getDefault().post(Event.OPEN_TARRAKKI_ZYAADA)
                } else if (referringParams?.optString("my_cart")?.contains("my_cart") == true) {
                    EventBus.getDefault().post(Event.OPEN_MY_CART)
                }
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                e("BRANCH SDK", error.message)
            }
        }
    }

}