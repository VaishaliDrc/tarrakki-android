package com.tarrakki.module.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.Menu
import android.widget.TextView
import com.tarrakki.*
import com.tarrakki.module.cart.CartFragment
import org.json.JSONObject
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
        //Branch.getInstance().initSession(BranchListener, this.intent.data, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager?.let {
            for (i in 1 until it.backStackEntryCount) {
                it.popBackStack()
            }
        }
        // Branch reinit (in case Activity is already in foreground when Branch link is clicked)
        //Branch.getInstance().reInitSession(this, BranchListener)
    }

    /*object BranchListener : Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject, error: BranchError?) {
            if (error == null) {
                e("BRANCH SDK", referringParams.toString())
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                e("BRANCH SDK", error.message)
            }
        }
    }*/

}