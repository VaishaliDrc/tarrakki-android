package com.tarrakki.module.netbanking

import android.arch.lifecycle.MutableLiveData
import org.supportcompact.FragmentViewModel

class NetBankingVM : FragmentViewModel() {

    val onPage = MutableLiveData<String>()
}