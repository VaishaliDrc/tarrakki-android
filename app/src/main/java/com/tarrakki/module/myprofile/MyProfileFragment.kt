package com.tarrakki.module.myprofile


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.Observable
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentMyProfileBinding
import kotlinx.android.synthetic.main.fragment_my_profile.*
import org.supportcompact.CoreFragment
import org.supportcompact.utilise.ImageChooserUtil

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyProfileFragment : CoreFragment<MyProfileVM, FragmentMyProfileBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.my_profile)

    override fun getLayout(): Int {
        return R.layout.fragment_my_profile
    }

    override fun createViewModel(): Class<out MyProfileVM> {
        return MyProfileVM::class.java
    }

    override fun setVM(binding: FragmentMyProfileBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnEdit?.setOnClickListener {
            getViewModel().isEdit.get()?.let { isEdit ->
                getViewModel().isEdit.set(!isEdit)
            }
        }
        getViewModel().isEdit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val isEdit = getViewModel().isEdit.get()!!
                if (isEdit) {
                    edtFName?.setSelection(edtFName.length())
                    edtFName?.requestFocus()
                }
            }
        })
        ivEditProfilePick?.setOnClickListener {
            // ImageChooserUtil.openChooserDialog(this, getViewModel().cvPhotoName, getViewModel().RQ_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ImageChooserUtil.PERMISSION_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageChooserUtil.openChooserDialog(this, getViewModel().cvPhotoName, getViewModel().RQ_CODE)
                }
            }
            ImageChooserUtil.REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageChooserUtil.startCameraIntent(this, getViewModel().cvPhotoName, getViewModel().RQ_CODE)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ImageChooserUtil.REQUEST_GALLERY,
            ImageChooserUtil.REQUEST_CAMERA -> {
                if (resultCode == RESULT_OK) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = MyProfileFragment().apply { arguments = basket }
    }
}
