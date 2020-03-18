package com.tarrakki.module.ekyc


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentEkycconfirmationBinding
import kotlinx.android.synthetic.main.fragment_ekycconfirmation.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [EKYCConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EKYCConfirmationFragment : CoreFragment<EKYCConfirmationVM, FragmentEkycconfirmationBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_ekycconfirmation
    }

    override fun createViewModel(): Class<out EKYCConfirmationVM> {
        return EKYCConfirmationVM::class.java
    }

    override fun setVM(binding: FragmentEkycconfirmationBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)
        btnYes?.setOnClickListener {
            startFragment(EKYCWebViewFragment.newInstance(), R.id.frmContainer)
            getViewModel().kycData?.let { data ->
                postSticky(data)
            }
        }

        btnNo?.setOnClickListener {
            onBack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData == null) {
            getViewModel().kycData = kycData
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment EKYCConfirmationFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = EKYCConfirmationFragment().apply { arguments = basket }
    }

}
