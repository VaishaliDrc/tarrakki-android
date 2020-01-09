package com.tarrakki.module.netbanking

import androidx.lifecycle.MutableLiveData
import org.supportcompact.FragmentViewModel

class NetBankingVM : FragmentViewModel() {

    val onPage = MutableLiveData<String>()

}