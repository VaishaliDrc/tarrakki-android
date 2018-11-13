package com.tarrakki.module.register

import android.databinding.ObservableField
import org.supportcompact.ActivityViewModel

class RegisterVM : ActivityViewModel() {

    val email = ObservableField("")
    val mobile = ObservableField("")
    val password = ObservableField("")
    val confirmPassword = ObservableField("")


}