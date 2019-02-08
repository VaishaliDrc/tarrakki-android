package com.tarrakki.module.bankmandate


import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.UserMandateDownloadResponse
import com.tarrakki.databinding.FragmentDownloadBankMandateFromBinding
import kotlinx.android.synthetic.main.fragment_download_bank_mandate_from.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.PermissionCallBack
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.toast
import org.supportcompact.networking.ApiClient
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

    var url : String? = ""
    var refid : Long? = 0
    var path = File(Environment.getExternalStorageDirectory().toString() + "/Tarrakki")

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
        val baseUrl = ApiClient.IMAGE_BASE_URL
        url = baseUrl+arguments?.getString("download_url").toString()

        loadPdf()

        btnDownload?.setOnClickListener {
            downloadPdf(url)
        }
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

    private fun onDownload(){
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
        request.setDestinationInExternalPublicDir("/Tarrakki",download_Uri.lastPathSegment)
        refid = downloadManager.enqueue(request)
    }

    var onComplete: BroadcastReceiver = object:BroadcastReceiver() {
        override fun onReceive(ctxt:Context, intent:Intent) {
            // get the refid from the download manager
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (refid == referenceId){
                toast("Download Complete.")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPdf(){
        mWebView?.settings?.javaScriptEnabled = true
        mWebView?.loadUrl("https://docs.google.com/viewer?embedded=true&url=$url")
    }

    private fun downloadPdf(url : String?) {
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
        //EventBus.getDefault().removeStickyEvent(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DownloadBankMandateFromFragment().apply { arguments = basket }
    }
}
