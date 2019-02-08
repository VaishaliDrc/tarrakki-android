package com.tarrakki.module.bankmandate

import android.databinding.ObservableField
import com.tarrakki.api.model.UserMandateDownloadResponse
import org.supportcompact.FragmentViewModel

class DownloadBankMandateFromVM : FragmentViewModel(){
    val mandateResponse = ObservableField<UserMandateDownloadResponse>()

}