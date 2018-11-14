package com.tarrakki.module.panverify

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class PANVerifyVM : FragmentViewModel() {

    val pan = ObservableField("")
    val dob = ObservableField("")
    val mobile = ObservableField("")
    val iAmIndian = ObservableField(true)
    val otp = ObservableField("")


}