package com.tarrakki.module.panverify


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentOtpPanverifyBinding
import kotlinx.android.synthetic.main.fragment_otp_panverify.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert

/**
 * A simple [Fragment] subclass.
 * Use the [OtpPANVerifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class OtpPANVerifyFragment : CoreFragment<PANVerifyVM, FragmentOtpPanverifyBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.verify_your_pan)

    override fun getLayout(): Int {
        return R.layout.fragment_otp_panverify
    }

    override fun createViewModel(): Class<out PANVerifyVM> {
        return PANVerifyVM::class.java
    }

    override fun setVM(binding: FragmentOtpPanverifyBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnSummit?.setOnClickListener {
            if (getViewModel().otp.get()?.length == 0) {
                context?.simpleAlert("Please enter OTP") {
                    edtOtp?.selectAll()
                    edtOtp?.requestFocus()
                }
            } else {
                for (i in 1..2) {
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }

        tvResendOtp?.setOnClickListener {
            context?.simpleAlert("OTP has benn resend successfully")
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment OtpPANVerifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = OtpPANVerifyFragment().apply { arguments = basket }
    }
}
