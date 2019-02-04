package com.tarrakki.module.ekyc

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class KYCRegistrationAVM : FragmentViewModel() {

    val fName = ObservableField("Himanshu Pratap")
    val dob = ObservableField("25 Nov, 1985")
    val guardian = ObservableField("Ravi Pratap")
    val guardianPANNumber = ObservableField("1ABCDE1234F")
    val addressType = ObservableField("Gift City, Gandhinagar")
    val address = ObservableField("Gift City, Gandhinagar")
    val city = ObservableField("Gandhinagar")
    val pincode = ObservableField("110001")
    val state = ObservableField("Gujarat")
    val country = ObservableField("India")
    val email = ObservableField("hinnanshu.pratap@gnnail.com")
    val mobile = ObservableField("9253493800")
    val PANNumber = ObservableField("1ABCDE1234F")
    val nominiName = ObservableField("Himanshu Pratap")
    val nominiRelationship = ObservableField("Father")
    val isEdit = ObservableField(true)
    val alpha = ObservableField<Float>(1f)
}