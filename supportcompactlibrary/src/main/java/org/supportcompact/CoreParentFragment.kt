package org.supportcompact

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.IntRange
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.events.Event
import org.supportcompact.ktx.PermissionCallBack
import org.supportcompact.ktx.checkPermissionRationale
import org.supportcompact.ktx.checkSelfPermissions
import org.supportcompact.ktx.requestAllPermissions

abstract class CoreParentFragment<VM : FragmentViewModel, DB : ViewDataBinding> : Fragment() {

    private lateinit var vm: VM
    private lateinit var binding: DB
    private var reference = false
    protected var coreActivityVM: ActivityViewModel? = null
    private val PERMISSION_CODE = 101
    private var permissionCallBack: PermissionCallBack? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            activity?.let {
                coreActivityVM = ViewModelProviders.of(it).get(ActivityViewModel::class.java)
            }
            parentFragment?.let {
                vm = ViewModelProviders.of(it).get(createViewModel())
            }
            setVM(binding)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!reference) {
            createReference()
            reference = true
        }
        coreActivityVM?.isEmpty?.value = false
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun createViewModel(): Class<out VM>

    abstract fun setVM(binding: DB)

    abstract fun createReference()

    fun getBinding(): DB = binding

    fun getViewModel(): VM = vm

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

    protected fun onBack() {
        activity?.onBackPressed()
    }

    protected fun onBack(@IntRange(from = 1, to = 100) steps: Int) {
        for (i in 1..steps) {
            activity?.supportFragmentManager?.popBackStack()
        }
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

    @Subscribe
    open fun onEvent(event: Event) {
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}