package com.tarrakki.module.changepassword

import android.databinding.ObservableField
import org.supportcompact.FragmentViewModel

class ChangePasswordVM : FragmentViewModel() {

    val currentPassword = ObservableField("")
    val newPassword = ObservableField("")
    val confirmPassword = ObservableField("")

}