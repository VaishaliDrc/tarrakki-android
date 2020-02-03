package com.tarrakki.module.birth_certificate


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tarrakki.R
import com.tarrakki.databinding.FragmentUploadDobcertiBinding
import com.tarrakki.getCustomUCropOptions
import com.tarrakki.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_upload_dobcerti.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [UploadDOBCertiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadDOBCertiFragment : CoreFragment<UploadDOBCertiVM, FragmentUploadDobcertiBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = "Birth Certificate"

    override fun getLayout(): Int {
        return R.layout.fragment_upload_dobcerti
    }

    override fun createViewModel(): Class<out UploadDOBCertiVM> {
        return UploadDOBCertiVM::class.java
    }

    override fun setVM(binding: FragmentUploadDobcertiBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        llUploadDOBC?.setOnClickListener {
            context?.takePick(
                    onGallery = {
                        openGallery()
                    },
                    onCamera = {
                        openCamera()
                    }
            )
        }
        btnNext?.setOnClickListener {
            if (getViewModel().uploadUri.isEmpty()) {
                context?.simpleAlert("Please upload birth certificate to farther proceed")
                return@setOnClickListener
            }
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent().apply {
                putExtra("img", getViewModel().uploadUri.get())
            })
            onBack()
        }
    }

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = "IMG_" + System.currentTimeMillis()
        destinationFileName += ".jpeg"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(3f, 2f)
        //uCrop.withMaxResultSize(800, 366)
        context?.getCustomUCropOptions()?.let { uCrop.withOptions(it) }
        uCrop.start(context!!, this)
    }

    private fun openCamera() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                ImageChooserUtil.startCameraIntent(this@UploadDOBCertiFragment,
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
                    if (data != null && icPreviewDoc != null) {
                        val imageUri = UCrop.getOutput(data)
                        getViewModel().uploadUri.set(imageUri?.let { getPath(it) })
                        icPreviewDoc.visibility = View.VISIBLE
                        val requestOptions = RequestOptions()
                        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                        requestOptions.skipMemoryCache(true)
                        Glide.with(icPreviewDoc)
                                .setDefaultRequestOptions(requestOptions)
                                .load(imageUri)
                                .into(icPreviewDoc)
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment UploadDOBCertiFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UploadDOBCertiFragment().apply { arguments = basket }
    }

}
