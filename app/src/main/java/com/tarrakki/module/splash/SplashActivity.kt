package com.tarrakki.module.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.tarrakki.R
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LoginActivity
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            if (isLogin()) {
                startActivity<HomeActivity>()
            } else {
                startActivity<LoginActivity>()
            }
            finish()
        }, 2500)
    }
}
