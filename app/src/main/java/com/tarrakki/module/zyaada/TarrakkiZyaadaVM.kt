package com.tarrakki.module.zyaada

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class TarrakkiZyaadaVM : FragmentViewModel() {

    val whatIsTarrakkiZyaada = ObservableField(true)
    val whereIsMyMoney = ObservableField(false)

}