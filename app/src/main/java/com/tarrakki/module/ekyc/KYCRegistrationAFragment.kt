package com.tarrakki.module.ekyc


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationABinding
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationAFragment.newInstance] factory method to
 * create an instance of this fragment.
 *s
 */
class KYCRegistrationAFragment : CoreFragment<KYCRegistrationAVM, FragmentKycregistrationABinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_kycregistration_a
    }

    override fun createViewModel(): Class<out KYCRegistrationAVM> {
        return KYCRegistrationAVM::class.java
    }

    override fun setVM(binding: FragmentKycregistrationABinding) {
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
         * @return A new instance of fragment KYCRegistrationAFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle) = KYCRegistrationAFragment().apply { arguments = basket }
    }
}
