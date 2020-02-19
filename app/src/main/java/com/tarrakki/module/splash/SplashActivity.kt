package com.tarrakki.module.splash

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.isTarrakki
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.intro.IntroductionActivity
import io.sentry.Sentry
import kotlinx.android.synthetic.main.activity_splash.*
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        clMain?.setBackgroundResource(if (BuildConfig.FLAVOR.isTarrakki()) R.drawable.splash else R.drawable.splash_rural)
//        printHasKey()
        //e(AES.decrypt("22wXlL93Gr46ttJkQKk+o894Wf2bODbIzEv8MleBnVDowT3mODh0COob3e8FRxF/H3WO84QcuYDsA7rxl94Y0g=="))
        Handler().postDelayed({
            /* if (isFirsttimeInstalled()) {
                 startActivity<IntroductionActivity>()
             }else{*/
            if (isLogin()) {
                App.INSTANCE.isAuthorise.value = false
                //App.INSTANCE.isLoggedIn.value = isLogin()
                startActivity<HomeActivity>()
            } else {
                startActivity<IntroductionActivity>()
                //startActivity<MaintenanceActivity>()
            }
            //  }
            finish()
        }, 2500)

    }
}