package com.tarrakki.module.splash

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import com.tarrakki.isTarrakki
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.intro.IntroductionActivity
import com.tarrakki.module.login.NewLoginActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.printHasKey
import org.supportcompact.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

       // clMain?.setBackgroundResource(if (BuildConfig.FLAVOR.isTarrakki()) R.drawable.splash_gif else R.drawable.splash_rural)
        printHasKey()
        //e(AES.decrypt("22wXlL93Gr46ttJkQKk+o894Wf2bODbIzEv8MleBnVDowT3mODh0COob3e8FRxF/H3WO84QcuYDsA7rxl94Y0g=="))

        if (BuildConfig.FLAVOR.isTarrakki()){
            videoSplash.visibility = View.VISIBLE

            /*val video  = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)
            videoSplash.setVideoURI(video)

            videoSplash.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                override  fun onCompletion(mp: MediaPlayer?) {

                }
            })
            videoSplash.start()*/
            Glide.with(this).load(R.drawable.splash_gif).into(videoSplash)
            startNextActivity(2000)
        }else{
            videoSplash.visibility = View.GONE
            clMain?.setBackgroundResource(R.drawable.splash_rural)
            startNextActivity(2500)
        }


    }

    private fun startNextActivity(delayTime:Long) {
        Handler().postDelayed({
            /* if (isFirsttimeInstalled()) {
                 startActivity<IntroductionActivity>()
             }else{*/
            if (isLogin()) {
                App.INSTANCE.isAuthorise.value = false
                //App.INSTANCE.isLoggedIn.value = isLogin()
                startActivity<HomeActivity>()
            } else {
               // startActivity<IntroductionActivity>()
                startActivity<NewLoginActivity>()
                //startActivity<MaintenanceActivity>()
            }
            //  }
            finish()
        }, delayTime)
    }
}