package com.tarrakki.module.support

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.api.model.SupportQueryListResponse
import org.supportcompact.FragmentViewModel

class SubQueriesVM : FragmentViewModel() {

    val query = MutableLiveData<SupportQueryListResponse.Data>()
    val queryTitle = ObservableField<String>()

}