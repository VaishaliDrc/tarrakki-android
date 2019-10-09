package com.tarrakki.module.verifybankaccount


import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tarrakki.R
import com.tarrakki.api.model.UserBanksResponse
import com.tarrakki.databinding.FragmentVerifyBankAccountBinding
import com.tarrakki.getCustomUCropOptions
import com.tarrakki.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_verify_bank_account.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.PermissionCallBack
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.takePick
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [VerifyBankAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class VerifyBankAccountFragment : CoreFragment<VerifyBankAccountVM, FragmentVerifyBankAccountBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.add_bank_account)
    private var doc_Id = ""
    var userBankData: UserBanksResponse? = null

    override fun getLayout(): Int {
        return R.layout.fragment_verify_bank_account
    }

    override fun createViewModel(): Class<out VerifyBankAccountVM> {
        return VerifyBankAccountVM::class.java
    }

    override fun setVM(binding: FragmentVerifyBankAccountBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        arguments?.let {
        }

        lluploadStatement.setOnClickListener {
            context?.takePick(
                    onGallery = {
                        openGallery()
                    },
                    onCamera = {
                        openCamera()
                    }
            )

        }

        llUploadCheck.setOnClickListener {
            context?.takePick(
                    onGallery = {
                        openGallery()
                    },
                    onCamera = {
                        openCamera()
                    }
            )

        }

        btnAdd?.setOnClickListener {
            if (getViewModel()?.uploadUri?.get().toString()?.equals("")) {
                context?.simpleAlert(getString(R.string.alert_verify_bank_account)) {
                }
            } else {
                if (userBankData != null)
                    getViewModel().uploadBankDoc(userBankData).observe(this, Observer {
                        context?.simpleAlert(getString(R.string.alert_success_new_bank)) {
                            onBack(2)
                            coreActivityVM?.onNewBank?.value = true
                        }

                    })
            }

        }


    }

    private fun openCamera() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                ImageChooserUtil.startCameraIntent(this@VerifyBankAccountFragment,
                        getViewModel().cvPhotoName, getViewModel().ICAMERA_RQ_CODE)
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
                        getViewModel()?.uploadUri.set(imageUri.toString())
                        icPreviewDoc.visibility = View.VISIBLE
                        Glide.with(icPreviewDoc).load(imageUri).into(icPreviewDoc)
                    }
                }
            }
        }
    }

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = doc_Id
        destinationFileName += ".jpeg"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(3f, 2f)
        //uCrop.withMaxResultSize(800, 366)
        context?.getCustomUCropOptions()?.let { uCrop.withOptions(it) }
        uCrop.start(context!!, this)
    }


    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = VerifyBankAccountFragment().apply {
            arguments = basket
            if (basket != null && basket.size() > 0) {
                userBankData = Gson().fromJson<UserBanksResponse>(basket.getString("userBankData"), UserBanksResponse::class.java)
            }

        }

    }
}
