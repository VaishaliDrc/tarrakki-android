package com.tarrakki.module.support.chat


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.View
import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.util.IOUtils
import com.tarrakki.*
import com.tarrakki.api.model.SupportViewTicketResponse
import com.tarrakki.databinding.FragmentChatBinding
import com.tarrakki.module.transactions.LoadMore
import com.tarrakki.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.inputclasses.keyboardListener
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ChatFragment : CoreFragment<ChatVM, FragmentChatBinding>() {

    val ticket = MutableLiveData<SupportViewTicketResponse.Data.Conversation>()

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.ticket_conversation)

    override fun getLayout(): Int {
        return R.layout.fragment_chat
    }

    override fun createViewModel(): Class<out ChatVM> {
        return ChatVM::class.java
    }

    override fun setVM(binding: FragmentChatBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        ticket.value?.let { App.INSTANCE.openChat = Pair(true, it.ticketRef ?: "") }
    }

    val allData = ArrayList<WidgetsViewModel>()
    val loadMore = LoadMore()

    override fun createReference() {
        rvChat?.setHasFixedSize(true)
        rvChat?.layoutManager?.isAutoMeasureEnabled = false
        activity?.keyboardListener { isOpen ->
            coreActivityVM?.footerVisibility?.set(View.GONE)
            getViewModel().btnAttahcmentVisibility.set(if (isOpen) View.GONE else View.VISIBLE)
        }
        ivSend?.setOnClickListener {
            if (edtMessage.text.toString().isNotBlank() && getViewModel().chatData.value?.data?.conversation?.isNotEmpty() == true) {
                ticket.value?.let { ticket ->
                    getViewModel().sendData(ticket, "${edtMessage.text}".trim()).observe(this, Observer {
                        getViewModel().getConversation(ticket)
                        edtMessage?.text?.clear()
                        edtMessage?.dismissKeyboard()
                    })
                }

            }
        }
        ivGallery?.setOnClickListener {
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

        ticket.observe(this, Observer { it ->
            it?.let { ticket ->
                getViewModel().reference.set(ticket.ticketRef)
                getViewModel().btnSendVisibility.set(ticket.open)
                val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissionsIfRequired(permissions, object : PermissionCallBack {
                    override fun permissionGranted() {
                        @Suppress("IMPLICIT_CAST_TO_ANY")
                        getViewModel().getConversation(ticket).observe(this@ChatFragment, Observer {
                            it?.data?.conversation?.let { chats ->
                                ticket.status = it.data.status
                                getViewModel().btnSendVisibility.set(ticket.open)
                                loadMore.isLoading = false
                                allData.clear()
                                allData.addAll(chats)
                                if (allData.size >= 10 && it.data.totalCount ?: allData.size > allData.size) {
                                    allData.add(loadMore)
                                }
                                if (rvChat?.adapter == null) {
                                    rvChat?.setUpMultiViewRecyclerAdapter(allData) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                                        binder.setVariable(BR.msg, item)
                                        binder.executePendingBindings()
                                        if (item is LoadMore && !item.isLoading) {
                                            loadMore.isLoading = true
                                            Handler().postDelayed({
                                                getViewModel().getConversation(ticket, position)
                                            }, 1500)
                                        }
                                    }
                                    /*rvChat?.post {
                                        Handler().postDelayed({
                                            rvChat?.scrollToPosition(0)
                                        }, 100)
                                    }*/
                                } else {
                                    rvChat?.post {
                                        rvChat?.adapter?.notifyItemRangeChanged(0, allData.size)
                                        /*if (it.data.offset == 0) {
                                            Handler().postDelayed({
                                                rvChat?.scrollToPosition(0)
                                            }, 100)
                                        }*/
                                    }
                                }
                            }
                        })
                    }

                    override fun permissionDenied() {
                        context?.confirmationDialog(
                                title = getString(R.string.permission),
                                msg = getString(R.string.write_external_storage_title),
                                btnPositive = getString(R.string.allow),
                                btnNegative = getString(R.string.dont_allow),
                                btnPositiveClick = {
                                    this@ChatFragment.ticket.postValue(ticket)
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
                ImageChooserUtil.startCameraIntent(this@ChatFragment,
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
        //val mFile = File(getPath(uri) ?: "")
        var destinationFileName = "img_issue".getUDID()
        destinationFileName += ".png"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        context?.getCustomUCropOptions()?.let { uCrop.withOptions(it) }
        uCrop.start(context!!, this)
    }

    private suspend fun createFile(@NonNull uri: Uri, outputFile: File) {
        withContext(Dispatchers.IO) {
            getViewModel().showProgress()
            try {
                val inputStream = context?.contentResolver?.openInputStream(uri)
                FileOutputStream(outputFile).use { outputStream -> IOUtils.copyStream(inputStream, outputStream, true, DEFAULT_BUFFER_SIZE) }
            } catch (e: IOException) {
                // handle exception here
                e.printStackTrace()
            } catch (e: Exception) {
                // handle exception here
                e.printStackTrace()
            } finally {
                GlobalScope.launch(Dispatchers.Main) {
                    ticket.value?.let { ticket ->
                        getViewModel().sendData(ticket).observe(this@ChatFragment, Observer {
                            getViewModel().getConversation(ticket)
                            edtMessage?.text?.clear()
                            edtMessage?.dismissKeyboard()
                        })
                    }
                }
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
                                    val fileName = "file_issue".getUDID()
                                            .plus(selectedUri.getFileName()?.replace(" ", "_"))
                                    val mFile = File(getFileDownloadDir(), fileName)
                                    getViewModel().sendFile = Pair(1, mFile)
                                    lifecycleScope.launch {
                                        createFile(selectedUri, mFile)
                                    }
                                } else {
                                    context?.simpleAlert(getString(R.string.max_file_size_msg))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        val imageUri = UCrop.getOutput(data)
                        imageUri?.let {
                            val mFile = File(getPath(it) ?: "")
                            getViewModel().sendFile = Pair(0, mFile)
                            edtMessage?.text?.clear()
                            edtMessage?.dismissKeyboard()
                            ticket.value?.let { ticket ->
                                getViewModel().sendData(ticket).observe(this, Observer {
                                    getViewModel().getConversation(ticket)
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        coreActivityVM?.footerVisibility?.set(View.GONE)
    }

    override fun onStop() {
        super.onStop()
        coreActivityVM?.footerVisibility?.set(View.VISIBLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.INSTANCE.openChat = null
    }

    @Subscribe(sticky = true)
    fun onReceive(data: SupportViewTicketResponse.Data.Conversation) {
        if (ticket.value == null) {
            ticket.value = data
        } else if (ticket.value?.ticketRef == data.ticketRef) {
            ticket.postValue(data)
        }
        //removeStickyEvent(data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ChatFragment().apply { arguments = basket }
    }
}