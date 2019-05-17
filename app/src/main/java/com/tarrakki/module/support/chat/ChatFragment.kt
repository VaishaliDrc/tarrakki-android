package com.tarrakki.module.support.chat


import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.arch.lifecycle.Observer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.view.View
import com.google.android.gms.common.util.IOUtils
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.SupportChatResponse
import com.tarrakki.api.model.SupportViewTicketResponse
import com.tarrakki.databinding.FragmentChatBinding
import com.tarrakki.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_chat.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.inputclasses.keyboardListener
import org.supportcompact.ktx.*
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : CoreFragment<ChatVM, FragmentChatBinding>() {

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

    override fun createReference() {
        activity?.keyboardListener { isOpen ->
            coreActivityVM?.footerVisibility?.set(View.GONE)
            if (isOpen) {
                /*Handler().postDelayed({
                    rvChat?.scrollToPosition(getViewModel().chats.size - 1)
                }, 300)*/
            }
        }
        ivSend?.setOnClickListener {
            /*if (edtMessage.text.toString().isNotBlank()) {
                getViewModel().chats.add(ChatMessage(
                        "${edtMessage.text}",
                        "${Date().convertTo("dd-MM-yyyy hh:mma")}",
                        getViewModel().chats.size % 2 == 0))
                edtMessage?.text?.clear()
                rvChat?.adapter?.notifyItemInserted(getViewModel().chats.size)
                edtMessage?.dismissKeyboard()
                Handler().postDelayed({
                    rvChat?.scrollToPosition(getViewModel().chats.size - 1)
                }, 300)
            }*/
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

        getViewModel().ticket.observe(this, Observer { it ->
            it?.let { ticket ->
                getViewModel().getConversation(ticket).observe(this, Observer {
                    it?.data?.conversation?.let { chats ->
                        rvChat?.setUpMultiViewRecyclerAdapter(chats) { item: SupportChatResponse.Data.Conversation, binder: ViewDataBinding, position: Int ->
                            binder.setVariable(BR.msg, item)
                            binder.executePendingBindings()
                        }
                        rvChat?.post {
                            rvChat?.scrollToPosition(chats.size - 1)
                        }
                    }
                })
            }
        })
        context?.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(onComplete)
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (referenceId != -1L) {
                val temp = getViewModel().chatData.value?.data?.conversation?.first { it.downloadReference == referenceId }
                temp?.txtOpen = R.string.open
            }
        }
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

        /*getViewModel().chats.add(ChatMessage(
                "",
                "${Date().convertTo("dd-MM-yyyy hh:mma")}",
                true,
                uri))
        edtMessage?.text?.clear()
        rvChat?.adapter?.notifyItemInserted(getViewModel().chats.size)
        edtMessage?.dismissKeyboard()
        Handler().postDelayed({
            rvChat?.smoothScrollToPosition(getViewModel().chats.size - 1)
        }, 750)*/
        /*val mFile = File(getPath(uri))
        //var destinationFileName = mFile.nameWithoutExtension
        //destinationFileName += ".${mFile.extension}"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, mFile.name)))
        context?.getCustomUCropOptions()?.let { uCrop.withOptions(it) }
        uCrop.start(context!!, this)*/
    }

    private fun createFile(@NonNull uri: Uri, outputFile: File) {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        try {
            FileOutputStream(outputFile).use { outputStream -> IOUtils.copyStream(inputStream, outputStream, true, DEFAULT_BUFFER_SIZE) }
        } catch (e: FileNotFoundException) {
            // handle exception here
            e.printStackTrace()
        } catch (e: IOException) {
            // handle exception here
            e.printStackTrace()
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
                        val fileName = selectedUri.getFileName()
                        val mFile = File(App.INSTANCE.cacheDir, "$fileName")
                        getViewModel().sendFile = mFile
                        createFile(selectedUri, mFile)
                    }
                }
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        val imageUri = UCrop.getOutput(data)
                        imageUri?.let {}
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

    @Subscribe(sticky = true)
    fun onReceive(data: SupportViewTicketResponse.Data.Conversation) {
        if (getViewModel().ticket.value == null) {
            getViewModel().ticket.value = data
        }
        removeStickyEvent(data)
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
