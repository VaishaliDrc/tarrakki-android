package com.tarrakki.module.ekyc


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.databinding.ObservableField
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationBBinding
import com.tarrakki.signatureDialog
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_kycregistration_b.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationBFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class KYCRegistrationBFragment : CoreFragment<KYCRegistrationBVM, FragmentKycregistrationBBinding>() {

    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_kycregistration_b
    }

    override fun createViewModel(): Class<out KYCRegistrationBVM> {
        return KYCRegistrationBVM::class.java
    }

    override fun setVM(binding: FragmentKycregistrationBBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        switchOnOff?.setOnCheckedChangeListener { buttonView, isChecked ->
            getViewModel().iCertify.set(isChecked)
        }
        edtSourceIncome?.setOnClickListener {
            context?.showListDialog(R.string.source_of_income, R.array.income_source) { item ->
                getViewModel().sourceOfIncome.set(item)
            }
        }
        edtIncomeSlab?.setOnClickListener {
            context?.showListDialog(R.string.income_slab, R.array.income_slab) { item ->
                getViewModel().TAXSlab.set(item)
            }
        }
        edtIssue?.setOnClickListener {
            showCountry(getViewModel().issueByA)
        }
        edtIssue1?.setOnClickListener {
            showCountry(getViewModel().issueByB)
        }
        edtIssue2?.setOnClickListener {
            showCountry(getViewModel().issueByC)
        }
        btnLogout?.setOnClickListener {
            if (isValid()) {
                context?.signatureDialog(
                        btnDigitally = {

                        },
                        btnPhysically = {
                            context?.takePick(
                                    onGallery = {
                                        openGallery()
                                    },
                                    onCamera = {
                                        openCamera()
                                    }
                            )
                        })

            }
        }
    }

    private fun openGallery() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*")
                        .addCategory(Intent.CATEGORY_OPENABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val mimeTypes = arrayOf("image/jpeg", "image/png")
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
                startActivityForResult(Intent.createChooser(intent, "Select File"), getViewModel().IMAGE_RQ_CODE)
                /*val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(pickPhoto, "Select File"), getViewModel().IMAGE_RQ_CODE)*/
            }

            override fun permissionDenied() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.allow),
                        btnNegative = getString(R.string.dont_allow),
                        btnPositiveClick = {
                            openGallery()
                        }
                )
            }

            override fun onPermissionDisabled() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.settings),
                        btnNegative = getString(R.string.cancel),
                        btnPositiveClick = {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context?.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                )
            }
        })
    }

    private fun openCamera() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                ImageChooserUtil.startCameraIntent(this@KYCRegistrationBFragment, getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
            }

            override fun permissionDenied() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.allow),
                        btnNegative = getString(R.string.dont_allow),
                        btnPositiveClick = {
                            openCamera()
                            //ImageChooserUtil.startCameraIntent(this@MyProfileFragment, getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
                        }
                )
            }

            override fun onPermissionDisabled() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.settings),
                        btnNegative = getString(R.string.cancel),
                        btnPositiveClick = {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context?.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                )
            }
        })
    }

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = SAMPLE_CROPPED_IMAGE_NAME
        destinationFileName += ".png"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(16f, 9f)
        uCrop.withMaxResultSize(50, 30)
        uCrop.start(context!!, this)
    }


    private fun showCountry(item: ObservableField<String>) {
        context?.showListDialog(R.string.select_country, R.array.countries) { country ->
            item.set(country)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                getViewModel().ICAMERA_RQ_CODE -> {
                    val file = ImageChooserUtil.getCameraImageFile(getViewModel().cvPhotoName)
                    startCrop(Uri.fromFile(file))
                }
                getViewModel().IMAGE_RQ_CODE -> {
                    val selectedUri = data?.data
                    if (selectedUri != null) {
                        startCrop(selectedUri)
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        val imageUri = UCrop.getOutput(data)
                        imageUri?.let {
                            if (it.scheme == "file") {

                            } else {

                            }
                            toast("File uploaded successfully")
                        }
                    }
                }
            }
        }
    }


    private fun isValid(): Boolean {
        return when {
            getViewModel().PANName.isEmpty() -> {
                context?.simpleAlert("Please enter PAN name")
                false
            }
            getViewModel().sourceOfIncome.isEmpty() -> {
                context?.simpleAlert("Please select source of income")
                false
            }
            getViewModel().TAXSlab.isEmpty() -> {
                context?.simpleAlert("Please select income slab")
                false
            }
            !switchOnOff.isChecked && getViewModel().TINNumberA.isEmpty() /*&&
                    getViewModel().TINNumberB.isEmpty() &&
                    getViewModel().TINNumberC.isEmpty()*/ -> {
                context?.simpleAlert("Please enter TIN number")
                false
            }
            !switchOnOff.isChecked && getViewModel().issueByA.isEmpty()/* &&
                    getViewModel().issueByB.isEmpty() &&
                    getViewModel().issueByC.isEmpty() */ -> {
                context?.simpleAlert("Please select country of issue")
                false
            }
            else -> true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment KYCRegistrationBFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = KYCRegistrationBFragment().apply { arguments = basket }
    }
}
