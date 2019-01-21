package com.tarrakki.module.resetPassword

import com.tarrakki.R
import android.os.Bundle
import com.tarrakki.BaseActivity
import com.tarrakki.module.changepassword.ChangePasswordFragment
import org.supportcompact.ktx.startFragment


class ResetPasswordActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent!=null) {
            val bundle = Bundle().apply {
                putString("token",intent?.extras?.getString("token"))
                putBoolean("isResetPassword",true)
            }
            startFragment(ChangePasswordFragment.newInstance(bundle), R.id.frmContainer)
        }
    }

    override fun onBackPressed() {
        getViewModel().isBackEnabled.value?.let {
            if (it)
                finish()
            else
                finish()
        }
    }
}
