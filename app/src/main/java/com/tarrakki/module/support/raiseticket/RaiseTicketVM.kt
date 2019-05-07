package com.tarrakki.module.support.raiseticket

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.tarrakki.api.model.SupportQueryListResponse
import org.supportcompact.FragmentViewModel

class RaiseTicketVM : FragmentViewModel() {

    val query = MutableLiveData<SupportQueryListResponse.Data>()

    val transaction = ObservableField("")
    val description = ObservableField("")
    val imgName = ObservableField("")
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "profilePick"

}