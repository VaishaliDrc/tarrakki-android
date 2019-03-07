package com.tarrakki.module.webviewActivity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.tarrakki.R
import com.tarrakki.databinding.ActivityWebviewBinding
import kotlinx.android.synthetic.main.activity_webview.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreActivity
import org.supportcompact.events.Event
import org.supportcompact.events.ShowError
import org.supportcompact.networking.ApiClient

class WebviewActivity : CoreActivity<WebviewVM, ActivityWebviewBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_webview
    }

    override fun createViewModel(): Class<out WebviewVM> {
        return WebviewVM::class.java
    }

    override fun setVM(binding: ActivityWebviewBinding) {

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun createReference() {

        btnClose?.setOnClickListener {
            onBackPressed()
        }

        mWebView.settings.javaScriptEnabled = true // enable javascript
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.loadsImagesAutomatically = true
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

        getViewModel().onPage.observe(this, Observer { it ->
            it?.let { page ->
                when (page) {
                    Event.NEFTRTGS -> {
                        mWebView?.loadUrl(ApiClient.IMAGE_BASE_URL+"/cart/payment/method/")
                    }
                    else -> {

                    }
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
    fun onUrl(event: Event) {
        getViewModel().onPage.value = event
    }
}
