package com.tarrakki.module.plan

import android.os.Bundle
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.module.goal.GoalFragment
import com.tarrakki.module.goal.canBack
import org.supportcompact.ktx.startFragment

class PlanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFragment(GoalFragment.newInstance(Bundle().apply { putBoolean(canBack, false) }), R.id.frmContainer)
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
