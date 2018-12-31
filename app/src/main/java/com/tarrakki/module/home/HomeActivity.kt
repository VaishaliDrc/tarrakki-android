package com.tarrakki.module.home

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.tarrakki.ACTION_FINISH_ALL_TASK
import com.tarrakki.BaseActivity
import com.tarrakki.R
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.startFragment

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}