package org.supportcompact

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class CoreFragment<VM : FragmentViewModel, DB : ViewDataBinding> : Fragment() {

    private lateinit var vm: VM
    private lateinit var binding: DB
    private var reference = false
    protected abstract val isBackEnabled: Boolean
    protected abstract val title: String
    protected var coreActivityVM: ActivityViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            activity?.let {
                coreActivityVM = ViewModelProviders.of(it).get(ActivityViewModel::class.java)
            }
            vm = ViewModelProviders.of(this).get(createViewModel())
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
        coreActivityVM?.isBackEnabled?.value = isBackEnabled
        coreActivityVM?.title?.set(title)
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun createViewModel(): Class<out VM>

    abstract fun setVM(binding: DB)

    abstract fun createReference()

    fun getBinding(): DB = binding

    fun getViewModel(): VM = vm
}