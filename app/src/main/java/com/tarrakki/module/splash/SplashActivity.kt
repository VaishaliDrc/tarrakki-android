package com.tarrakki.module.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.api.AES
import org.supportcompact.aescrypt.AESCrypt
import org.supportcompact.ktx.e
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val data = AES.encrypt("username=admin&password=Drc@1234gh")
//        val data = AESCrypt.encrypt(App.INSTANCE.getString(R.string.key), "username=admin&password=Drc@1234gh")
        e("Data=>", data)
        val ddata = AES.decrypt(data)
        e("DData=>", ddata)
        Handler().postDelayed({
            if (isLogin()) {
                startActivity<HomeActivity>()
            } else {
                startActivity<LoginActivity>()
            }
//            startActivity<HomeActivity>()
            finish()
        }, 2500)
    }
}
