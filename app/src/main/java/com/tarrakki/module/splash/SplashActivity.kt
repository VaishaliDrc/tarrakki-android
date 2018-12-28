package com.tarrakki.module.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.module.home.HomeActivity
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //e(AES.decrypt("22wXlL93Gr46ttJkQKk+o894Wf2bODbIzEv8MleBnVDowT3mODh0COob3e8FRxF/H3WO84QcuYDsA7rxl94Y0g=="))
        Handler().postDelayed({
            App.INSTANCE.isAuthorise.value = false
            App.INSTANCE.isLoggedIn.value = isLogin()
            startActivity<HomeActivity>()
            finish()
        }, 2500)
    }
}
