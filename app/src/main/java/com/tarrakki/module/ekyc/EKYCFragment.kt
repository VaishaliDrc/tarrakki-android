package com.tarrakki.module.ekyc


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentEkycBinding
import kotlinx.android.synthetic.main.fragment_ekyc.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.startFragment


/**
 * A simple [Fragment] subclass.
 * Use the [EKYCFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EKYCFragment : CoreFragment<EKYCVM, FragmentEkycBinding>() {


    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.e_kyc)

    override fun getLayout(): Int {
        return R.layout.fragment_ekyc
    }

    override fun createViewModel(): Class<out EKYCVM> {
        return EKYCVM::class.java
    }

    override fun setVM(binding: FragmentEkycBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnContinue?.setOnClickListener {
            getViewModel().kycData?.let { kycData ->
                startFragment(EKYCWebViewFragment.newInstance(), R.id.frmContainer)
                postSticky(kycData)
            }
        }
        ivEKYC?.setImageDrawable(context?.getDrawable(R.drawable.ekyc))
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
         * @return A new instance of fragment EKYCFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = EKYCFragment().apply { arguments = Bundle().apply { arguments = basket } }
    }
}
