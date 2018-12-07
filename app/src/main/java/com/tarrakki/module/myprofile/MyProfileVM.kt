package com.tarrakki.module.myprofile

import android.databinding.Observable
import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class MyProfileVM : FragmentViewModel() {

    val fName = ObservableField("Himanshu Pratap")
    val dob = ObservableField("25 Nov, 1985")
    val guardian = ObservableField("Ravi Pratap")
    val address = ObservableField("Gift City, Gandhinagar")
    val city = ObservableField("Gandhinagar")
    val pincode = ObservableField("110001")
    val state = ObservableField("Gujarat")
    val country = ObservableField("India")
    val email = ObservableField("hinnanshu.pratap@gnnail.com")
    val mobile = ObservableField("9253493800")
    val PANNumber = ObservableField("1ABCDE1234F")
    val occupation = ObservableField("Business")
    val nominiName = ObservableField("Himanshu Pratap")
    val nominiRelationship = ObservableField("Father")
    val isEdit = ObservableField(false)
    val alpha = ObservableField<Float>(0.4f)
    val cvPhotoName = "profilePick"
    val IMAGE_RQ_CODE = 101
    val ICAMERA_RQ_CODE = 181

    init {
        isEdit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isEdit.get()?.let {
                    alpha.set(if (it) 1f else 0.4f)
                }
            }
        })
    }

}