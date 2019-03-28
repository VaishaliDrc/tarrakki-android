package com.tarrakki.module.netbanking


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.webkit.*
import com.tarrakki.R
import com.tarrakki.databinding.FragmentNetBankingBinding
import com.tarrakki.module.paymentmode.ISFROMTRANSACTIONMODE
import com.tarrakki.module.paymentmode.SUCCESSTRANSACTION
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import kotlinx.android.synthetic.main.fragment_net_banking.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.e
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [NetBankingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val NET_BANKING_PAGE = "net_banking_page"

class NetBankingFragment : CoreFragment<NetBankingVM, FragmentNetBankingBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.net_banking)

    override fun getLayout(): Int {
        return R.layout.fragment_net_banking
    }

    override fun createViewModel(): Class<out NetBankingVM> {
        return NetBankingVM::class.java
    }

    override fun setVM(binding: FragmentNetBankingBinding) {

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun createReference() {

        val newUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0"
        mWebView.settings.userAgentString = newUA
        mWebView.clearCache(true)
        mWebView.settings.javaScriptEnabled = true // enable javascript
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.setAppCachePath(context?.cacheDir?.absolutePath)
        mWebView.settings.setAppCacheEnabled(true)
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebView.settings.setSupportMultipleWindows(false)
        mWebView.settings.builtInZoomControls = true

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
                e("URL=>$url")
                return when {
                    url.startsWith("http://tarrakkilive.edx.drcsystems.com/api/v1/transactions/payment-status/") -> {
                        val bundle = Bundle().apply {
                            arguments?.getString(SUCCESSTRANSACTION)?.let { it1 -> putString(SUCCESSTRANSACTION, it1) }
                            arguments?.getBoolean(ISFROMTRANSACTIONMODE)?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                            putBoolean(NET_BANKING_PAGE, true)
                        }
                        startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                        view.loadUrl(url)
                        true
                    }
                    else -> {
                        view.loadUrl(url)
                        true
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

        getViewModel().onPage.observe(this, Observer {
            it?.let { page ->
                mWebView?.loadData(page, "text/html", "UTF-8")
            }
        })

        arguments?.getString(NET_BANKING_PAGE)?.let { page ->
            getViewModel().onPage.value = page
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.
         * @return A new instance of fragment NetBankingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = NetBankingFragment().apply { arguments = basket }
    }
}
