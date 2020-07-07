package com.tarrakki.module.support.raiseticket


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.common.util.IOUtils
import com.tarrakki.R
import com.tarrakki.api.model.SupportQueryListResponse
import com.tarrakki.api.model.SupportQuestionListResponse
import com.tarrakki.api.model.TransactionApiResponse
import com.tarrakki.databinding.FragmentRaiseTicketBinding
import com.tarrakki.getCustomUCropOptions
import com.tarrakki.getFileDownloadDir
import com.tarrakki.module.support.SupportFragment
import com.tarrakki.module.transactions.TransactionsFragment
import com.tarrakki.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_raise_ticket.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass.
 * Use the [RaiseTicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

const val IS_FROM_RAISE_TICKET = "is_from_raise_ticket"

class RaiseTicketFragment : CoreFragment<RaiseTicketVM, FragmentRaiseTicketBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.raise_ticket)

    override fun getLayout(): Int {
        return R.layout.fragment_raise_ticket
    }

    override fun createViewModel(): Class<out RaiseTicketVM> {
        return RaiseTicketVM::class.java
    }

    override fun setVM(binding: FragmentRaiseTicketBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        //val transactions = arrayListOf("154782 - SBI Banking and Financial Services")
        tvChooseTransaction?.setOnClickListener {
            startFragment(TransactionsFragment.newInstance(Bundle().apply { putBoolean(IS_FROM_RAISE_TICKET, true) }), R.id.frmContainer)
            /*context?.showCustomListDialog(getString(R.string.choose_transaction), transactions) { item ->
                getViewModel().transaction.set(item)
            }*/
        }
        openGallery?.setOnClickListener {
            context?.takePick(
                    onGallery = {
                        openGallery()
                    },
                    onCamera = {
                        openCamera()
                    }
            )
        }
        openFile?.setOnClickListener {
            openDocumentFile()
        }
        switchOnOff?.setOnCheckedChangeListener { buttonView, isChecked ->
            getViewModel().transactionVisibility.set(if (isChecked) View.VISIBLE else View.GONE)
            getViewModel().transactionVisibiSwitch.set(isChecked)
        }
        getViewModel().query.observe(this, Observer { query ->
            query?.let {
                //getViewModel().checkTransactionStatus(query)
                btnSubmit?.setOnClickListener {
                    when {
                        getViewModel().transactionVisibility.get() == View.VISIBLE && getViewModel().transaction.isEmpty() -> {
                            context?.simpleAlert(getString(R.string.alert_ticket_issue_transaction))
                        }
                        getViewModel().description.isEmpty() -> {
                            context?.simpleAlert(getString(R.string.alert_ticket_issue_description))
                        }
                        else -> {
                            getViewModel().submitTicket(query).observe(this, Observer {
                                it?.let {
                                    context?.simpleAlert(getString(R.string.alert_ticket_issued)) {
                                        onBackExclusive(SupportFragment::class.java)
                                    }
                                }
                            })
                        }
                    }
                }
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

    private fun openDocumentFile() {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                val mimeTypes = arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain", "application/pdf", "application/zip")
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .setType("*/*")
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

                startActivityForResult(Intent.createChooser(intent, "Select File"), getViewModel().FILE_RQ_CODE)
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
                ImageChooserUtil.startCameraIntent(this@RaiseTicketFragment,
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

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = "issue_image".getUDID()
        destinationFileName += ".png"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        context?.getCustomUCropOptions()?.let { uCrop.withOptions(it) }
        uCrop.start(context!!, this)
    }

    private fun createFile(@NonNull uri: Uri, outputFile: File) {
        thread {
            getViewModel().showProgress()
            try {
                val inputStream = context?.contentResolver?.openInputStream(uri)
                FileOutputStream(outputFile).use { outputStream -> IOUtils.copyStream(inputStream, outputStream, true, DEFAULT_BUFFER_SIZE) }
            } catch (e: FileNotFoundException) {
                // handle exception here
                e.printStackTrace()
            } catch (e: IOException) {
                // handle exception here
                e.printStackTrace()
            } finally {
                getViewModel().dismissProgress()
            }
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
                getViewModel().FILE_RQ_CODE -> {
                    val selectedUri = data?.data
                    if (selectedUri != null) {
                        try {
                            val cursor = context?.contentResolver?.query(selectedUri, null, null, null, null)
                            cursor?.moveToFirst()
                            val size = cursor?.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                            cursor?.close()
                            size?.let {
                                val filesize = it
                                val filesizeInKB = filesize / 1024
                                val filesizeinMB = filesizeInKB / 1024
                                if (filesizeinMB < 25) {
                                    val fileName = "ticket_file".getUDID()
                                            .plus(selectedUri.getFileName()?.replace(" ", "_"))
                                    getViewModel().imgName.set(fileName)
                                    val mFile = File(getFileDownloadDir(), fileName)
                                    getViewModel().sendFile = Pair(1, mFile)
                                    createFile(selectedUri, mFile)
                                } else {
                                    context?.simpleAlert(getString(R.string.max_file_size_msg))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        /*getViewModel().imgName.set(selectedUri.getFileName()?.replace(" ", "_")
                                ?: "")
                        val mFile = File(getFileDownloadDir(), "${getViewModel().imgName.get()}")
                        getViewModel().sendFile = Pair(1, mFile)
                        createFile(selectedUri, mFile)*/
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        val imageUri = UCrop.getOutput(data)
                        imageUri?.let {
                            val mFile = File(getPath(it) ?: "")
                            getViewModel().imgName.set(imageUri.getFileName() ?: mFile.name)
                            ivUploadPic?.setImageURI(it)
                            getViewModel().sendFile = Pair(0, mFile)
                        }
                    }
                }
            }
        }
    }

    @Subscribe(sticky = true)
    fun onReceived(data: SupportQueryListResponse.Data) {
        if (getViewModel().query.value == null) {
            getViewModel().query.value = data
        }
        removeStickyEvent(data)
    }

    @Subscribe(sticky = true)
    fun onReceived(data: SupportQuestionListResponse.Question) {
        if (getViewModel().question.get() == null) {
            getViewModel().question.set(data)
        }
        removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: TransactionApiResponse.Transaction) {
        if (getViewModel().transactionData.get() == null) {
            getViewModel().transactionData.set(data)
            getViewModel().transaction.set(data.name)
        }
        removeStickyEvent(data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param bundle As Bundle.
         * @return A new instance of fragment RaiseTicketFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) = RaiseTicketFragment().apply { arguments = bundle }
    }
}
