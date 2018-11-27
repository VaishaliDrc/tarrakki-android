package com.tarrakki.module.myprofile

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class MyProfileVM : FragmentViewModel() {

    val fName = ObservableField("Himanshu")
    val lName = ObservableField("Pratap")
    val email = ObservableField("hinnanshu.pratap@gnnail.com")
    val mobile = ObservableField("9253493800")
    val PANNumber = ObservableField("1ABCDE1234F")
    val isEdit = ObservableField(false)
    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181

}