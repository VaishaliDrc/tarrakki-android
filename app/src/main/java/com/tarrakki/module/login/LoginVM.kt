package com.tarrakki.module.login

import android.databinding.ObservableField
import org.supportcompact.ActivityViewModel

class LoginVM : ActivityViewModel() {

    val userName = ObservableField("")
    val password = ObservableField("")

}