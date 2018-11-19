package com.tarrakki.module.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.osquare.security.AES
import com.tarrakki.R
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.login.LoginActivity
import org.supportcompact.aescrypt.AESCrypt
import org.supportcompact.ktx.isLogin
import org.supportcompact.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        AESCrypt.decrypt("test", "1dxSb6jo1vaBglxDuiP8og1LpqtQrBm16ZWLPQ/FJzeixCh0ckG6CVRJe8JoI+lmJVDqOXNXw/nD00IrS2kkCLDeeSCsmCrstb1B9Inkhw1ALXiITHvLAQYQmbyZ0J4X4thKMM1fvrvpr9gUDUsP243nZlo16Y5QnWKP6nNfsqOZGmUR7IimIhzOgxRiugH8eSAQkwk59AdoUWvIbsKrJJY3y991Y/wSMuNI4EqS+DU54Jlv3ODLCNdaa2nNtsOenBd0Mj3dw10FlO/6vu4p6QBLRHf2W0xxlB61R8rBoLIDwO4XXauxg2fJpNy1x4dZAOjKuVMujUhAcfFSkP7w+wXDPDYhUkj/cj6NiAKXZoOCs8kCLqOWWsu9scvJGMRD9ey+QBM9r0mO/y9+dDSVpJ/7JB906WNRLL4xIJ8f4FRhlLkKu74EfDEAhrEqbw0LNMn4NbdBDoQ7nnTXE609zkm1ICv6Thv+TNXIKv5+LC5VgbuhBWMhtJAOzPiW03qmSCWLZ01Q3Y+ppH61JOGSCv65uI9MS2ibjrb3Z2vduKfwHnggzaNFMyiZC1ZBaT1QFy+dJPHpCvZAPNnEfL0l3BgHM0gUBVuYZQ2mXyiGpyBSAZ09s3RS3EZXgXBESZO918INuodkYw7StjcGpfwMqe7/6Kg1tz2z7kqplWYKLFTTK/bb6rEnkus8N6Enl1/XA67vsisEB8SC9Xxo8irh+XxxrIzU2dpnQY67VkHW2mNMkiVyAMMy0bMtImyFNEY7t85zffpBejAYxGsaua8VL4zYluIu7z4fWiiufdTvTzoODX73hPPlWGTogsqFCU8oVxwEu3myfUBDRrE/iv5XqC/Yx5FEqr8DNsMWJ+BMiTcLgRRHbOfQwBBY5eBvs4O5IENUvI+nY04qNUblhD9928W8tAG5bkvk4tlcZPMAetUSJVa5e2qj5G6mOCUjAEcYbe+JuQFjWgvuy3mczZiyBvQBrhGkdhlpBy2vFMI7Zj5u4KVQ//bQuAJslftsVyocP61ZgMgMgoFN6eydqLKBhyrX7ZG0AyztOsZl4v1tV6TGUQgurR65+/8k5p61wUOcP7kJLfdrbC17Sv1rMsL4gm7k4T0CC+ZyZe8PXd4MtUCPHYxkDgE9OYQID0h5zXwnAlfiadI2mVBCiiIShkzZfJCUGZiYMbnZ6vkk3P2dwaPoFRwWL0bXZWzYhEnQCmprPz7xww5NrGzry7i0Flu8YeBrmn2aVj2QeNlJkq/33l9fEQmQ3RLt8k/vZlliFXKDgElpJdaugrq9VbfQ7FO0e9i+Jhir6S+OPPvkGoO0QDmoJagPdBhYRaB7x0PLFyLBaXy6P1ntiyFVoR0pS9TReTrWOqZsevms8c512PwzjiuQ1O0vaEP2Nxs4TrrE1Brpkox1AIR+6gMOztHZuwCNHE8gUlln27ue3QSYpAI6nTT9zHn6NBAXzI7DhcyzWEfwtJB1uVQYzX4+3KAVNjmAmaV36ihwaZzZq9+POAChQij1Td/uh9bKu5oiW3WcJdExrlD9Jfr1eNPlbHlnSUVHfv+L48BkJs85TQkYNecMjE6xuokeoygfV6F/Va/clVi4")
        AES.decrypt("ZApVTfOgFltMs9LO5tdwIw4NfveE8+VYZOiCyoUJTyhXHAS7ebJ9QENGsT+K/leoL9jHkUSqvwM2wxYn4EyJNwuBFEds59DAEFjl4G+zg7l3IgmOKgeUXcyARIgc6n+5MzK467IhadCE7yaVLllyRa3UDaE+h9w4047q7oBjZ73Napq4rMLl/0XwpIXL5lnp")
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
