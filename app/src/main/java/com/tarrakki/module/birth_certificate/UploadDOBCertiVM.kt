package com.tarrakki.module.birth_certificate

import androidx.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class UploadDOBCertiVM : FragmentViewModel() {
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181
    val cvPhotoName = "verifyAccountPic"
    val uploadUri = ObservableField<String>()
}