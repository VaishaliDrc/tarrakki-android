package com.tarrakki.module.ekyc


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentKycregistrationBBinding
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [KYCRegistrationBFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class KYCRegistrationBFragment : CoreFragment<KYCRegistrationBVM, FragmentKycregistrationBBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.complete_registration)

    override fun getLayout(): Int {
        return R.layout.fragment_kycregistration_b
    }

    override fun createViewModel(): Class<out KYCRegistrationBVM> {
        return KYCRegistrationBVM::class.java
    }

    override fun setVM(binding: FragmentKycregistrationBBinding) {
    }

    override fun createReference() {
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment KYCRegistrationBFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle) = KYCRegistrationBFragment().apply { arguments = basket }
    }
}
