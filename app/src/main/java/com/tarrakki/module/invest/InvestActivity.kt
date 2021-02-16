package com.tarrakki.module.invest

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import com.gocashfree.cashfreesdk.CFPaymentService
import com.tarrakki.App
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.homeInvest.HomeInvestFragment
import org.supportcompact.ktx.cartCount
import org.supportcompact.ktx.postError
import org.supportcompact.ktx.startFragment

class InvestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFragment(HomeInvestFragment.newInstance(), R.id.frmContainer)
    }

    override fun onBackPressed() {
        getViewModel().isBackEnabled.value?.let {
            if (it)
                super.onBackPressed()
            else
                finish()
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager?.let {
            for (i in 1 until it.backStackEntryCount) {
                it.popBackStack()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Same request code for all payment APIs.
//        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        if (requestCode == CFPaymentService.REQ_CODE) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    postSticky(bundle)
                } else {
                    postError(R.string.something_went_wrong)
                }
            }
        }
    }
}
