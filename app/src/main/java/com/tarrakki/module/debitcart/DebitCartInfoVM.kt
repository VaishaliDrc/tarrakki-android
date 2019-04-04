package com.tarrakki.module.debitcart

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class DebitCartInfoVM : FragmentViewModel() {

    val folioNo = ObservableField<String>()
    val cardHolerName = ObservableField<String>()
    val mothersName = ObservableField<String>()
    val dob = ObservableField<String>()

}