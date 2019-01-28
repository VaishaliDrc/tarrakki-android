package com.tarrakki.module.webview


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Base64
import android.view.View
import android.webkit.*
import com.tarrakki.R
import com.tarrakki.api.WebserviceBuilder
import com.tarrakki.databinding.FragmentWebViewBinding
import kotlinx.android.synthetic.main.fragment_web_view.*
import org.supportcompact.CoreFragment
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.e
import org.supportcompact.networking.ApiClient
import org.supportcompact.networking.SingleCallback
import org.supportcompact.networking.subscribeToSingle
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.webkit.PermissionRequest


/**
 * A simple [Fragment] subclass.
 * Use the [WebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WebViewFragment : CoreFragment<WebViewVM, FragmentWebViewBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = "E-KYC"

    override fun getLayout(): Int {
        return R.layout.fragment_web_view
    }

    override fun createViewModel(): Class<out WebViewVM> {
        return WebViewVM::class.java
    }

    override fun setVM(binding: FragmentWebViewBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun createReference() {

        val builder = Uri.Builder()
        builder.appendQueryParameter("url", "c")
        builder.appendQueryParameter("session_id", "")
        builder.appendQueryParameter("ekyctype", "I")
        builder.appendQueryParameter("plkyc_type", "INVESTOR")
        builder.appendQueryParameter("kyc_data", "AJTPG6148B|sajan.gandhi@ia.ooo|9727244400|com.tarrakki.app|PLUTONOMIC_INVESTOR|AU82#bx|PA|MFKYC3|SESS_ID")


        mWebView.settings.javaScriptEnabled = true // enable javascript
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.allowContentAccess = true
        mWebView.settings.allowFileAccess = true
        mWebView.settings.allowUniversalAccessFromFileURLs = true
        mWebView.settings.domStorageEnabled = true
        //mWebView.settings.pluginState = WebSettings.PluginState.ON
        mWebView.settings.setAppCacheEnabled(false)
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.grant(request.resources)
                }
                mWebView.reload()
                //super.onPermissionRequest(request)
            }
        }

        mWebView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                if (url.startsWith("tel:")) {
                    initiateCall(url)
                    return true
                }
                if (url.startsWith("mailto:")) {
                    sendEmail(url.substring(7))
                    return true
                }

                view.loadUrl(url)
                return false
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()

                if (url.startsWith("tel:")) {
                    initiateCall(url)
                    return true
                }
                if (url.startsWith("mailto:")) {
                    sendEmail(url.substring(7))
                    return true
                }

                view.loadUrl(url)
                return false
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE

            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE

            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                progressBar.visibility = View.GONE

            }
        }

        /* val postData = "url=https://cdc.camsonline.com/GETMethod/GetMethod.aspx" +
                 "&session_id=" +
                 "&ekyctype=I" +
                 "&plkyc_type=INVESTOR" +
                 "&kyc_data=AJTPG6148B|sajan.gandhi@ia.ooo|9727244400|com.tarrakki.app|PLUTONOMIC_INVESTOR|AU82#bx|PA|MFKYC3|SESS_ID"

         mWebView.postUrl("https://eiscuat1.camsonline.com/PLKYC/Home/home/", Base64.encode(postData.toByteArray(Charsets.UTF_8), Base64.DEFAULT))
 */
        subscribeToSingle(ApiClient.getApiClient("https://eiscuat1.camsonline.com/")
                .create(WebserviceBuilder::class.java)
                .eKYC("https://cdc.camsonline.com/GETMethod/GetMethod.aspx",
                        "",
                        "I",
                        "INVESTOR",
                        "AJTPG6148B|sajan.gandhi@ia.ooo|9727244400|com.tarrakki.app|PLUTONOMIC_INVESTOR|AU82#bx|PA|MFKYC3|SESS_ID"), WebserviceBuilder.ApiNames.addGoal, object : SingleCallback<WebserviceBuilder.ApiNames> {
            override fun onSingleSuccess(o: Any?, apiNames: WebserviceBuilder.ApiNames) {
                // e("Response=>${o?.toString()}")
                mWebView.loadDataWithBaseURL(
                        "https://eiscuat1.camsonline.com/PLKYC/Home/home",
                        "${o?.toString()}",
                        "text/html",
                        null,
                        null)
            }

            override fun onFailure(throwable: Throwable, apiNames: WebserviceBuilder.ApiNames) {
                throwable.printStackTrace()
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
        fun newInstance(basket: Bundle? = null) = WebViewFragment().apply { arguments = basket }
    }
}
