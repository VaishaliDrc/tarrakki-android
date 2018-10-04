package com.tarrakki.module.learn

import android.os.Bundle
import com.tarrakki.BaseActivity
import com.tarrakki.R
import org.supportcompact.ktx.startFragment

class LearnActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFragment(LearnFragment.newInstance(), R.id.frmContainer)
    }

    override fun onBackPressed() {
        getViewModel().isBackEnabled.value?.let {
            if (it)
                super.onBackPressed()
            else
                finish()
        }
    }
}
