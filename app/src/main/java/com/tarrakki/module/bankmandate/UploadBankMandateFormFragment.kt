package com.tarrakki.module.bankmandate


import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.api.model.UserMandateDownloadResponse
import com.tarrakki.databinding.FragmentUploadloadBankMandateFormBinding
import kotlinx.android.synthetic.main.fragment_uploadload_bank_mandate_form.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.startFragment
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [UploadBankMandateFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UploadBankMandateFormFragment : CoreFragment<UploadBankMandateFormVM, FragmentUploadloadBankMandateFormBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    override fun getLayout(): Int {
        return R.layout.fragment_uploadload_bank_mandate_form
    }

    override fun createViewModel(): Class<out UploadBankMandateFormVM> {
        return UploadBankMandateFormVM::class.java
    }

    override fun setVM(binding: FragmentUploadloadBankMandateFormBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().uploadUri.set(arguments?.getString("upload_url"))
        img_preview?.setImageURI(Uri.parse(getViewModel().uploadUri.get()))
        btnSubmit?.setOnClickListener {
            getViewModel().uploadMandateForm(arguments?.getString(MANDATEID)?.toInt()).observe(this, Observer {
                val bundle = Bundle().apply {
                    arguments?.getBoolean(ISFROMBANKMANDATE)?.let { it1 -> putBoolean(ISFROMBANKMANDATE, it1) }
                }
                startFragment(BankMandateSuccessFragment.newInstance(bundle), R.id.frmContainer)
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: UserMandateDownloadResponse) {
        getViewModel().mandateResponse.set(data)
        EventBus.getDefault().removeStickyEvent(data)
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UploadBankMandateFormFragment().apply { arguments = basket }
    }
}
