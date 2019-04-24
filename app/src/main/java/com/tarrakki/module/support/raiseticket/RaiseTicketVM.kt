package com.tarrakki.module.support.raiseticket

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class RaiseTicketVM : FragmentViewModel() {

    val transaction = ObservableField("")
    val description = ObservableField("")
    val imgName = ObservableField("")
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "profilePick"

}