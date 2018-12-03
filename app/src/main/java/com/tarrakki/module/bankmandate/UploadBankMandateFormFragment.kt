package com.tarrakki.module.bankmandate


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentUploadloadBankMandateFormBinding
import kotlinx.android.synthetic.main.fragment_uploadload_bank_mandate_form.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.startFragment

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
        btnSubmit?.setOnClickListener {
            activity?.supportFragmentManager?.let { manager ->
                for (i in 1..4) {
                    manager.popBackStack()
                }
            }
            EventBus.getDefault().post(Event.BANK_MANDATE_SUBMITTED)
            startFragment(BankMandateSuccessFragment.newInstance(), R.id.frmContainer)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment DownloadBankMandateFromFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = UploadBankMandateFormFragment().apply { arguments = basket }
    }
}
