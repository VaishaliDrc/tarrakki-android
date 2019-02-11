package com.tarrakki.module.bankmandate

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.ViewDataBinding
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.webkit.*
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.UserMandateDownloadResponse
import com.tarrakki.databinding.FragmentBankMandateFormBinding
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_bank_mandate_form.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.PermissionCallBack
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.takePick
import org.supportcompact.networking.ApiClient
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File

class BankMandateFormFragment : CoreFragment<BankMandateFormVM, FragmentBankMandateFormBinding>() {
    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"
    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate_form
    }

    override fun createViewModel(): Class<out BankMandateFormVM> {
        return BankMandateFormVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateFormBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().isIMandate.set(arguments?.getBoolean(ISIPMANDATE))

        if (getViewModel().isIMandate.get() == false) {
            var selectedAt = 0
            rvBankMandateForm?.setUpMultiViewRecyclerAdapter(getViewModel().bankMandateWays) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.widget, item)
                binder.setVariable(BR.onAdd, View.OnClickListener {
                    if (selectedAt == 0){
                        val mandateId = getViewModel().mandateResponse.get()?.data?.id
                        getViewModel().getMandateForm(mandateId).observe(this, Observer {
                            response->
                            val bundle = Bundle().apply {
                                putString("download_url",response?.data?.mandateFile)
                            }
                            startFragment(DownloadBankMandateFromFragment.newInstance(bundle), R.id.frmContainer)
                        })
                    }else{
                        context?.takePick(
                                onGallery = {
                                    openGallery()
                                },
                                onCamera = {
                                    openCamera()
                                }
                        )
                        //
                    }
                })
                binder.root.setOnClickListener {
                    if (item is BankMandateWay) {
                        val data = getViewModel().bankMandateWays[selectedAt]
                        if (data is BankMandateWay) {
                            data.isSelected = false
                        }
                        item.isSelected = true
                        selectedAt = position
                    }
                }
                binder.executePendingBindings()
            }
        }else{
            loadWebView(arguments?.getString(IMANDATEDATA).toString())
            //mWebView?.loadData(arguments?.getString(IMANDATEDATA), "text/html", null)
        }

        btnContinue?.setOnClickListener {
                val bundle = Bundle().apply {
                    putBoolean(ISIPMANDATE, true)
                }
                startFragment(BankMandateSuccessFragment.newInstance(bundle), R.id.frmContainer)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(url : String){
        mWebView.settings.javaScriptEnabled = true // enable javascript
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.setAppCachePath(context?.cacheDir?.absolutePath)
        mWebView.settings.setAppCacheEnabled(true)
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebView.settings.setSupportMultipleWindows(false)

        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar?.progress = newProgress
            }
        }

        mWebView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return onPageRequest(view, url)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return onPageRequest(view, request.url.toString())
            }

            fun onPageRequest(view: WebView, url: String): Boolean {
                return when {
                    url.startsWith("tel:") -> {
                        initiateCall(url)
                        true
                    }
                    url.startsWith("mailto:") -> {
                        sendEmail(url.substring(7))
                        true
                    }
                    else -> {
                        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            startActivity(this)
                        }
                        return true
                    }
                }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar?.visibility = View.GONE
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                progressBar?.visibility = View.GONE
            }
        }
        mWebView?.loadDataWithBaseURL(
                ApiClient.IMAGE_BASE_URL,
                url,
                "text/html",
                "UTF-8",
                null)

       // mWebView?.loadUrl(url)
    }

    private fun sendEmail(add: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(add))
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            post(ShowError("There are no email clients installed."))
        }

    }

    private fun initiateCall(url: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            post(ShowError("Error Dialling"))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: UserMandateDownloadResponse) {
        getViewModel().mandateResponse.set(data)
        //EventBus.getDefault().removeStickyEvent(data)
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
                ImageChooserUtil.startCameraIntent(this@BankMandateFormFragment,
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
        var destinationFileName = SAMPLE_CROPPED_IMAGE_NAME
        destinationFileName += ".jpg"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(16f, 9f)
        val options = UCrop.Options()
        options.setMaxBitmapSize(2097152)
        options.setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        options.setActiveWidgetColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        uCrop.withOptions(options)
        uCrop.start(context!!, this)
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
                            val bundle = Bundle().apply {
                                putString("upload_url",imageUri.toString())
                                putBoolean(ISFROMBANKMANDATE,false)
                            }
                            /* if (it.scheme == "file") {
                                 val myBitmap = BitmapFactory.decodeFile(it.path)
                                 ivProfile?.setImageBitmap(myBitmap)
                             } else {
                                 ivProfile?.setImageURI(it)
                             }*/
                            startFragment(UploadBankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFormFragment().apply { arguments = basket }
    }
}
