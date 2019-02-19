package com.tarrakki.module.webview


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.webkit.*
import com.tarrakki.R
import com.tarrakki.databinding.FragmentWebViewBinding
import kotlinx.android.synthetic.main.fragment_web_view.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.events.ShowError
import org.supportcompact.networking.ApiClient


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
        get() = getString(R.string.e_kyc)

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
                    url.startsWith("http://115.160.244.10:8084") -> {
                        view.loadUrl(url)
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

        getViewModel().onPage.observe(this, Observer { it ->
            it?.let { page ->
                when (page) {
                    Event.PRIVACY_PAGE -> {
                        coreActivityVM?.title?.set(context?.getString(R.string.privacy_policy))
                        mWebView?.loadUrl(ApiClient.IMAGE_BASE_URL+"/privacy/")
                    }
                    Event.TERMS_AND_CONDITIONS_PAGE -> {
                        coreActivityVM?.title?.set(context?.getString(R.string.terms_and_condditions))
                        mWebView?.loadUrl(ApiClient.IMAGE_BASE_URL+"/tos/")
                    }
                    else -> {
                    }
                    /*Event.PRIVACY_PAGE -> {
                        coreActivityVM?.title?.set(context?.getString(R.string.privacy_policy))
                        mWebView?.loadUrl("http://115.160.244.10:8084/zalak/odoo-projects/tarraki/mobile-tarraki/privacy-pliocy.html")
                    }
                    Event.TERMS_AND_CONDITIONS_PAGE -> {
                        coreActivityVM?.title?.set(context?.getString(R.string.terms_and_condditions))
                        mWebView?.loadUrl("http://115.160.244.10:8084/zalak/odoo-projects/tarraki/mobile-tarraki/termofservice.html")
                    }
                    else -> {
                    }*/
                }
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

    @Subscribe(sticky = true)
    override fun onEvent(event: Event) {
        super.onEvent(event)
        getViewModel().onPage.value = event
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
