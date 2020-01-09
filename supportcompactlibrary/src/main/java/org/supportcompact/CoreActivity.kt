package org.supportcompact

import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.events.Maintenance
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.*

abstract class CoreActivity<VM : ActivityViewModel, DB : ViewDataBinding> : AppCompatActivity() {

    private lateinit var vm: VM
    private lateinit var binding: DB
    private val PERMISSION_CODE = 111
    private var permissionCallBack: PermissionCallBack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayout())
        vm = ViewModelProviders.of(this).get(createViewModel())
        setVM(binding)
        createReference()
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun createViewModel(): Class<out VM>

    abstract fun setVM(binding: DB)

    abstract fun createReference()

    fun getBinding(): DB = binding

    fun getViewModel(): VM = vm

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    fun post(data: Any) {
        EventBus.getDefault().post(data)
    }

    fun postSticky(data: Any) {
        EventBus.getDefault().postSticky(data)
    }

    fun removeStickyEvent(data: Any) {
        EventBus.getDefault().removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun showDialog(show: String) {
        when (show) {
            SHOW_PROGRESS -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                getViewModel().progressBar.set(View.VISIBLE)
            }
            DISMISS_PROGRESS -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                getViewModel().progressBar.set(View.GONE)
            }
        }
        EventBus.getDefault().removeStickyEvent(show)
    }

    var isShowing = false
    @Subscribe
    fun showError(error: ShowError) {
        if (!isShowing) {
            isShowing = true
            simpleAlert(error.error) { isShowing = false }
        }
        //binding.root.snackBar(error.error)
    }

    @Subscribe
    fun <T : Activity> showMaintenance(m: Maintenance<T>) {
        getViewModel().dismissProgress()
        startActivity(Intent(this, m.aClass).putExtra(MAINTENANCE_END_TIME, m.endTime))
    }

    protected fun requestPermissionsIfRequired(permissions: ArrayList<String>, permissionCallBack: PermissionCallBack?) {
        this.permissionCallBack = permissionCallBack
        if (checkSelfPermissions(permissions)) {
            permissionCallBack?.permissionGranted()
        } else {
            requestAllPermissions(permissions, PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack?.permissionGranted()
                } else {
                    if (checkPermissionRationale(permissions)) {
                        permissionCallBack?.permissionDenied()
                    } else {
                        permissionCallBack?.onPermissionDisabled()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (getViewModel().progressBar.get() == View.GONE)
            super.onBackPressed()
    }
}
