package com.tarrakki.module.support

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableField
import com.tarrakki.api.model.SupportQueryListResponse
import org.supportcompact.FragmentViewModel

class SubQueriesVM : FragmentViewModel() {

    val query = MutableLiveData<SupportQueryListResponse.Data>()
    val queryTitle = ObservableField<String>()

}