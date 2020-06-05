package com.tarrakki.module.bankmandate


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.api.model.UserMandateDownloadResponse
import com.tarrakki.databinding.FragmentDownloadBankMandateFromBinding
import com.tarrakki.module.webview.PAGE_URL
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_download_bank_mandate_from.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadBankMandateFromFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DownloadBankMandateFromFragment : CoreFragment<DownloadBankMandateFromVM, FragmentDownloadBankMandateFromBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var url: String? = ""
    var mandate_id: String? = ""
    var refid: Long? = 0
    var path = File(Environment.getExternalStorageDirectory().toString() + "/Tarrakki")
    var instructionUrl = ""

    override fun getLayout(): Int {
        return R.layout.fragment_download_bank_mandate_from
    }

    override fun createViewModel(): Class<out DownloadBankMandateFromVM> {
        return DownloadBankMandateFromVM::class.java
    }

    override fun setVM(binding: FragmentDownloadBankMandateFromBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        val baseUrl = ""//ApiClient.IMAGE_BASE_URL
        url = baseUrl + arguments?.getString("download_url").toString()
        mandate_id = arguments?.getString("mandate_id").toString()

        loadPdf()

        btnDownload?.setOnClickListener {
            downloadPdf(url)
        }

        btnMandateSendEmail?.setOnClickListener {
            getViewModel().sendMandateForm(mandate_id).observe(this, Observer {
                context?.simpleAlert(getString(R.string.alert_nach_mandate_email_send))
            })
        }

        val readMore = object : ClickableSpan() {

            override fun onClick(widget: View) {
                instructionUrl = "${getViewModel().mandateResponse.get()?.data?.instructionUrl}"
                startFragment(WebViewFragment.newInstance(Bundle().apply { putString(PAGE_URL, instructionUrl) }), R.id.frmContainer)
                postSticky(Event.BANK_MANDATE_INSTRUCTIONS)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                context?.color(R.color.colorAccent)?.let { ds.color = it }
            }
        }
        tvInstruction?.makeLinks(arrayOf("Instruction"), arrayOf(readMore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.registerReceiver(onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(onComplete)
    }

    private fun onDownload() {
        if (!path.exists()) {
            path.mkdirs()
        }
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val download_Uri = Uri.parse(url)
        val request = DownloadManager.Request(download_Uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(false)
        request.setTitle("Tarrakki Downloading " + download_Uri.lastPathSegment)
        request.setDescription("Downloading " + download_Uri.lastPathSegment)
        request.setVisibleInDownloadsUi(true)
        request.setDestinationInExternalPublicDir("/Tarrakki", download_Uri.lastPathSegment)
        refid = downloadManager.enqueue(request)
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (refid == referenceId) {
                context?.simpleAlert(getString(R.string.alert_nach_mandate_download_success))
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPdf() {
        mWebView.settings.javaScriptEnabled = true // enable javascript
        mWebView.settings.loadWithOverviewMode = true
        // mWebView.settings.useWideViewPort = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.loadsImagesAutomatically = true
        //mWebView.settings.setAppCachePath(context?.cacheDir?.absolutePath)
        // mWebView.settings.setAppCacheEnabled(true)
        //  mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
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
        mWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$url")
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

    private fun downloadPdf(url: String?) {
        val permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionsIfRequired(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                onDownload()
            }

            override fun permissionDenied() {
                context?.confirmationDialog(
                        title = getString(R.string.permission),
                        msg = getString(R.string.write_external_storage_title),
                        btnPositive = getString(R.string.allow),
                        btnNegative = getString(R.string.dont_allow),
                        btnPositiveClick = {
                            downloadPdf(url)
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: UserMandateDownloadResponse) {
        getViewModel().mandateResponse.set(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DownloadBankMandateFromFragment().apply { arguments = basket }
    }
}
