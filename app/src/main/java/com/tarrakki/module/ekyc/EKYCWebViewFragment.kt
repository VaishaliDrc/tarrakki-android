package com.tarrakki.module.ekyc


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.databinding.FragmentEkycWebViewBinding
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_ekyc_web_view.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.PermissionCallBack
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.utilise.ImageChooserUtil
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [EKYCWebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EKYCWebViewFragment : CoreFragment<EKYCWebViewVM, FragmentEkycWebViewBinding>() {

    private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_ekyc_web_view
    }

    override fun createViewModel(): Class<out EKYCWebViewVM> {
        return EKYCWebViewVM::class.java
    }

    override fun setVM(binding: FragmentEkycWebViewBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun createReference() {

        var needToRedirect = false
        mWebView.clearCache(true)
        mWebView.settings.javaScriptEnabled = true // enable javascript
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.allowContentAccess = true
        mWebView.settings.allowUniversalAccessFromFileURLs = true
        mWebView.settings.domStorageEnabled = true

        //mWebView.settings.setAppCacheMaxSize(10 * 1024 * 1024) // 10MB

        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.setAppCachePath(context?.cacheDir?.absolutePath)
        mWebView.settings.allowFileAccess = true
        mWebView.settings.setAppCacheEnabled(true)
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar?.progress = newProgress
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                activity?.runOnUiThread {
                    request?.grant(request.resources)
                }
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                getViewModel().filePathCallback?.onReceiveValue(null)
                getViewModel().filePathCallback = null
                getViewModel().filePathCallback = filePathCallback
                openGallery()
                return true
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

            fun onPageRequest(mWebView: WebView, url: String): Boolean {
                return when {
                    url.startsWith("tel:") -> {
                        initiateCall(url)
                        true
                    }
                    url.startsWith("mailto:") -> {
                        sendEmail(url.substring(7))
                        true
                    }
                    url.startsWith(getViewModel().redirectUrl) -> {
                        needToRedirect = true
                        true
                    }
                    else -> false
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

        /*mWebView.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();") { html ->
            // code here
            if (needToRedirect) {
                try {
                    val doc = Jsoup.parse(html)
                    val values = doc.select("input[name=result]").attr("value").split("|")
                    if (values.isNotEmpty() && values.contains("Y")) {

                    } else {
                        post(ShowError(values[3]))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }*/

        requireActivity().onBackPressedDispatcher.addCallback(this@EKYCWebViewFragment, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack()
                } else {
                    context?.confirmationDialog(getString(R.string.are_you_sure_you_want_to_exit),
                            btnPositiveClick = {
                                onBack(2)
                            }
                    )
                }
            }
        })

        /*if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return
        }*/

        getViewModel().kycData.observe(this, Observer { it ->
            it?.let {
                mWebView?.loadUrl(it.mobileAutoLoginUrl)
                //mWebView?.loadData(it.mobileAutoLoginUrl, "text/html", "UTF-8")
                /*getViewModel().getEKYCPage(it).observe(this, Observer { apiResponse ->
                    apiResponse?.let { eKYCPage ->
                        val permissions = arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
                            override fun permissionGranted() {
                                mWebView?.loadDataWithBaseURL(
                                        "https://eiscuat1.camsonline.com/PLKYC/Home/home",
                                        eKYCPage,
                                        "text/html",
                                        "UTF-8",
                                        null)
                            }

                            override fun permissionDenied() {
                                context?.confirmationDialog(
                                        title = getString(R.string.permission),
                                        msg = getString(R.string.write_external_storage_title),
                                        btnPositive = getString(R.string.allow),
                                        btnNegative = getString(R.string.dont_allow),
                                        btnPositiveClick = {
                                            getViewModel().getEKYCPage(it).value = eKYCPage
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
                })*/
            }
        })
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

    private fun startCrop(@NonNull uri: Uri) {
        var destinationFileName = SAMPLE_CROPPED_IMAGE_NAME
        destinationFileName += ".png"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(context?.cacheDir, destinationFileName)))
        uCrop.withAspectRatio(16f, 9f)
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
                            //getViewModel().filePathCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                            getViewModel().filePathCallback?.onReceiveValue(arrayOf(imageUri))
                            getViewModel().filePathCallback = null
                        }
                    }
                }
            }
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData.value == null) {
            getViewModel().kycData.value = kycData
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.
         * @return A new instance of fragment WebViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = EKYCWebViewFragment().apply { arguments = basket }
    }
}
