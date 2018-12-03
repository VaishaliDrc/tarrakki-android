package com.tarrakki.module.bankmandate


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentDownloadBankMandateFromBinding
import org.supportcompact.CoreFragment

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
        fun newInstance(basket: Bundle? = null) = DownloadBankMandateFromFragment().apply { arguments = basket }
    }
}
